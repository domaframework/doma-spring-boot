/*
 * Copyright (C) 2004-2016 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.boot.autoconfigure;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.seasar.doma.jdbc.*;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.dialect.MysqlDialect;
import org.seasar.doma.jdbc.dialect.PostgresDialect;
import org.seasar.doma.jdbc.dialect.StandardDialect;
import org.seasar.doma.message.Message;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class DomaAutoConfigurationTest {
	AnnotationConfigApplicationContext context;

	@Before
	public void setUp() {
		this.context = new AnnotationConfigApplicationContext();
	}

	@Test
	public void testAutoRegisteredConfig() {
		this.context.register(DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		Config config = this.context.getBean(Config.class);
		assertThat(config, is(notNullValue()));
		assertThat(config.getDataSource(),
				is(instanceOf(TransactionAwareDataSourceProxy.class)));
		assertThat(config.getDialect(), is(instanceOf(StandardDialect.class)));
		assertThat(config.getSqlFileRepository(),
				is(instanceOf(GreedyCacheSqlFileRepository.class)));
		assertThat(config.getNaming(), is(Naming.DEFAULT));
		assertThat(config.getJdbcLogger(), is(instanceOf(UtilLoggingJdbcLogger.class)));
		PersistenceExceptionTranslator translator = this.context
				.getBean(PersistenceExceptionTranslator.class);
		assertThat(translator, is(instanceOf(DomaPersistenceExceptionTranslator.class)));
	}

	@Test
	public void testConfigWithDomaConfigBuilder() {
		this.context.register(ConfigBuilderConfigure.class, DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		Config config = this.context.getBean(Config.class);
		assertThat(config, is(notNullValue()));
		assertThat(config.getDataSource(),
				is(instanceOf(TransactionAwareDataSourceProxy.class)));
		assertThat(config.getDialect(), is(instanceOf(MysqlDialect.class)));
		assertThat(config.getSqlFileRepository(),
				is(instanceOf(NoCacheSqlFileRepository.class)));
		assertThat(config.getNaming(), is(Naming.SNAKE_UPPER_CASE));
		assertThat(config.getJdbcLogger(), is(instanceOf(UtilLoggingJdbcLogger.class)));
		PersistenceExceptionTranslator translator = this.context
				.getBean(PersistenceExceptionTranslator.class);
		assertThat(translator, is(instanceOf(DomaPersistenceExceptionTranslator.class)));
	}

	@Test
	public void testConfigWithConfig() {
		this.context.register(ConfigConfigure.class, DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		Config config = this.context.getBean(Config.class);
		assertThat(config, is(notNullValue()));
		assertThat(config.getDataSource(),
				is(instanceOf(TransactionAwareDataSourceProxy.class)));
		assertThat(config.getDialect(), is(instanceOf(PostgresDialect.class)));
		assertThat(config.getSqlFileRepository(),
				is(instanceOf(NoCacheSqlFileRepository.class)));
		assertThat(config.getNaming(), is(Naming.SNAKE_LOWER_CASE));
		assertThat(config.getJdbcLogger(), is(instanceOf(UtilLoggingJdbcLogger.class)));
		PersistenceExceptionTranslator translator = this.context
				.getBean(PersistenceExceptionTranslator.class);
		assertThat(translator, is(instanceOf(DomaPersistenceExceptionTranslator.class)));
	}

	@Test
	public void testSQLExceptionTranslator() {
		this.context.register(DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		PersistenceExceptionTranslator translator = this.context
				.getBean(PersistenceExceptionTranslator.class);
		{
			// Translated by SQLErrorCodeSQLExceptionTranslator
			DataAccessException dataAccessException = translator
					.translateExceptionIfPossible(new JdbcException(Message.DOMA2008,
							new SQLException("Acquire Lock on H2", "SqlState", 50200,
									null)));
			assertThat(dataAccessException,
					is(instanceOf(CannotAcquireLockException.class)));
		}
		{
			// Translated by SQLExceptionSubclassTranslator(fallback)
			DataAccessException dataAccessException = translator
					.translateExceptionIfPossible(new JdbcException(Message.DOMA2008,
							new SQLTimeoutException("Timeout", "SqlState", -1, null)));
			assertThat(dataAccessException, is(instanceOf(QueryTimeoutException.class)));
		}
		{
			// Translated by SQLStateSQLExceptionTranslator (fallback)
			DataAccessException dataAccessException = translator
					.translateExceptionIfPossible(new JdbcException(Message.DOMA2008,
							new SQLException("With check violation", "44", -1, null)));
			assertThat(dataAccessException,
					is(instanceOf(DataIntegrityViolationException.class)));
		}
	}

	@After
	public void tearDown() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Configuration
	public static class ConfigBuilderConfigure {
		@Bean
		DomaConfigBuilder myDomaConfigBuilder() {
			return new DomaConfigBuilder().dialect(new MysqlDialect())
					.sqlFileRepository(new NoCacheSqlFileRepository())
					.naming(Naming.SNAKE_UPPER_CASE);
		}
	}

	@Configuration
	public static class ConfigConfigure {
		@Bean
		Config myConfig(DataSource dataSource) {
			return new Config() {
				@Override
				public DataSource getDataSource() {
					return new TransactionAwareDataSourceProxy(dataSource);
				}

				@Override
				public Dialect getDialect() {
					return new PostgresDialect();
				}

				@Override
				public SqlFileRepository getSqlFileRepository() {
					return new NoCacheSqlFileRepository();
				}

				@Override
				public Naming getNaming() {
					return Naming.SNAKE_LOWER_CASE;
				}
			};
		}
	}

}
