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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seasar.doma.boot.DomaPersistenceExceptionTranslator;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.EntityListenerProvider;
import org.seasar.doma.jdbc.GreedyCacheSqlFileRepository;
import org.seasar.doma.jdbc.JdbcException;
import org.seasar.doma.jdbc.JdbcLogger;
import org.seasar.doma.jdbc.Naming;
import org.seasar.doma.jdbc.NoCacheSqlFileRepository;
import org.seasar.doma.jdbc.UtilLoggingJdbcLogger;
import org.seasar.doma.jdbc.SqlFileRepository;
import org.seasar.doma.jdbc.UtilLoggingJdbcLogger;
import org.seasar.doma.jdbc.criteria.Entityql;
import org.seasar.doma.jdbc.criteria.NativeSql;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.dialect.H2Dialect;
import org.seasar.doma.jdbc.dialect.MysqlDialect;
import org.seasar.doma.jdbc.dialect.PostgresDialect;
import org.seasar.doma.jdbc.dialect.StandardDialect;
import org.seasar.doma.message.Message;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

public class DomaAutoConfigurationTest {
	AnnotationConfigApplicationContext context;

	@BeforeEach
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
		assertThat(config.getEntityListenerProvider(), is(notNullValue()));
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
		assertThat(config.getEntityListenerProvider(),
				is(instanceOf(TestEntityListenerProvider.class)));
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
		assertThat(config.getEntityListenerProvider(),
				is(instanceOf(TestEntityListenerProvider.class)));
		PersistenceExceptionTranslator translator = this.context
				.getBean(PersistenceExceptionTranslator.class);
		assertThat(translator, is(instanceOf(DomaPersistenceExceptionTranslator.class)));
	}

	@Test
	public void testExceptionTranslationEnabledSpecifyFalse() {
		EnvironmentTestUtils.addEnvironment(this.context,
				"doma.exception-translation-enabled:false");
		this.context.register(DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		assertThrows(NoSuchBeanDefinitionException.class,
				() -> this.context.getBean(PersistenceExceptionTranslator.class));
	}

	@Test
	public void testExceptionTranslationEnabledSpecifyTrue() {
		EnvironmentTestUtils.addEnvironment(this.context,
				"doma.exception-translation-enabled:true");
		this.context.register(DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		PersistenceExceptionTranslator translator = this.context
				.getBean(PersistenceExceptionTranslator.class);
		assertThat(translator, is(instanceOf(DomaPersistenceExceptionTranslator.class)));
	}

	@Test
	public void testChangeDialect() {
		EnvironmentTestUtils.addEnvironment(this.context, "doma.dialect:MYSQL");
		this.context.register(DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		Dialect dialect = this.context.getBean(Dialect.class);
		assertThat(dialect, is(instanceOf(MysqlDialect.class)));
	}

	@Test
	public void testChangeMaxRows() {
		EnvironmentTestUtils.addEnvironment(this.context, "doma.max-rows:100");
		this.context.register(DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		Config config = this.context.getBean(Config.class);
		assertThat(config.getMaxRows(), is(100));
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

	@Test
	public void testAutoRegisteredCriteriaAPI() {
		this.context.register(DomaAutoConfiguration.class, DataSourceAutoConfiguration.class);
		this.context.refresh();
		Entityql entityql = this.context.getBean(Entityql.class);
		assertNotNull(entityql);
		NativeSql nativeSql = this.context.getBean(NativeSql.class);
		assertNotNull(nativeSql);
	}

	@Test
	public void testCriteriaAPIWithConfig() {
		this.context.register(MyCriteriaAPIConfig.class, DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		Map<String, Entityql> entityqlBeans = this.context.getBeansOfType(Entityql.class);
		assertEquals(1, entityqlBeans.size());
		assertNotNull(entityqlBeans.get("myEntityql"));
		Map<String, NativeSql> nativeSqlBeans = this.context.getBeansOfType(NativeSql.class);
		assertEquals(1, nativeSqlBeans.size());
		assertNotNull(nativeSqlBeans.get("myNativeSql"));
	}

	@Test
	public void testDialectByDataSourceUrl() {
		MutablePropertySources sources = context.getEnvironment()
				.getPropertySources();
		sources.addFirst(new MapPropertySource("test",
				Collections.singletonMap("spring.datasource.url", "jdbc:h2:mem:example")));
		this.context.register(DomaAutoConfiguration.class, DataSourceAutoConfiguration.class);
		this.context.refresh();
		Dialect dialect = this.context.getBean(Dialect.class);
		assertThat(dialect, is(instanceOf(H2Dialect.class)));
	}

	@Test
	public void testDialectByDomaPropertiesIgnoreDataSourceUrl() {
		MutablePropertySources sources = context.getEnvironment()
				.getPropertySources();
		Map<String, Object> source = new HashMap<>();
		source.put("spring.datasource.url", "jdbc:h2:mem:example");
		source.put("doma.dialect", "POSTGRES");
		sources.addFirst(new MapPropertySource("test", source));
		this.context.register(DomaAutoConfiguration.class, DataSourceAutoConfiguration.class);
		this.context.refresh();
		Dialect dialect = this.context.getBean(Dialect.class);
		assertThat(dialect, is(instanceOf(PostgresDialect.class)));
	}

	@AfterEach
	public void tearDown() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Configuration
	public static class ConfigBuilderConfigure {
		@Bean
		DomaConfigBuilder myDomaConfigBuilder(DomaProperties domaProperties) {
			return new DomaConfigBuilder(domaProperties).dialect(new MysqlDialect())
					.sqlFileRepository(new NoCacheSqlFileRepository())
					.jdbcLogger(new UtilLoggingJdbcLogger())
					.naming(Naming.SNAKE_UPPER_CASE)
					.entityListenerProvider(new TestEntityListenerProvider());
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

				@Override
				public JdbcLogger getJdbcLogger() {
					return new UtilLoggingJdbcLogger();
				}

				@Override
				public EntityListenerProvider getEntityListenerProvider() {
					return new TestEntityListenerProvider();
				}
			};
		}
	}

	static class TestEntityListenerProvider implements EntityListenerProvider {
	}

	private static class EnvironmentTestUtils {
		public static void addEnvironment(ConfigurableApplicationContext context,
				String pair) {
			MutablePropertySources sources = context.getEnvironment()
					.getPropertySources();
			String[] split = pair.split(":");
			String key = split[0];
			String value = split[1];
			sources.addFirst(new MapPropertySource("test", Collections.singletonMap(key,
					value)));
		}
	}

	@Configuration
	public static class MyCriteriaAPIConfig {

		@Bean
		public Entityql myEntityql(Config config) {
			return new Entityql(config);
		}

		@Bean
		public NativeSql myNativeSql(Config config) {
			return new NativeSql(config);
		}
	}
}
