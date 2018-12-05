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

import javax.sql.DataSource;
import org.seasar.doma.boot.DomaPersistenceExceptionTranslator;
import org.seasar.doma.boot.TryLookupEntityListenerProvider;
import org.seasar.doma.boot.event.DomaEventEntityListener;
import org.seasar.doma.boot.event.DomaEventListenerFactory;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.EntityListenerProvider;
import org.seasar.doma.jdbc.Naming;
import org.seasar.doma.jdbc.SqlFileRepository;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} for Doma.
 *
 * @author Toshiaki Maki
 * @author Kazuki Shimizu
 */
@Configuration
@ConditionalOnClass(Config.class)
@EnableConfigurationProperties(DomaProperties.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class DomaAutoConfiguration {
	@Autowired
	DomaProperties domaProperties;

	@Bean
	@ConditionalOnMissingBean
	public Dialect dialect() {
		return domaProperties.getDialect().create();
	}

	@Bean
	@ConditionalOnProperty(prefix = DomaProperties.DOMA_PREFIX, name = "exception-translation-enabled", matchIfMissing = true)
	public PersistenceExceptionTranslator exceptionTranslator(Config config) {
		return new DomaPersistenceExceptionTranslator(
				new SQLErrorCodeSQLExceptionTranslator(config.getDataSource()));
	}

	@Bean
	@ConditionalOnMissingBean
	public SqlFileRepository sqlFileRepository() {
		return domaProperties.getSqlFileRepository().create();
	}

	@Bean
	@ConditionalOnMissingBean
	public Naming naming() {
		return domaProperties.getNaming().naming();
	}

	@Bean
	public static DomaEventListenerFactory domaEventListenerFactory() {
		return new DomaEventListenerFactory();
	}

	@Bean
	public DomaEventEntityListener domaEventEntityListener() {
		return new DomaEventEntityListener();
	}

	@Bean
	@ConditionalOnMissingBean
	public EntityListenerProvider tryLookupEntityListenerProvider() {
		return new TryLookupEntityListenerProvider();
	}

	@Bean
	@ConditionalOnMissingBean
	public DomaConfigBuilder domaConfigBuilder() {
		return new DomaConfigBuilder();
	}

	@Bean
	@ConditionalOnMissingBean(Config.class)
	public DomaConfig config(DataSource dataSource, Dialect dialect,
			SqlFileRepository sqlFileRepository, Naming naming,
			EntityListenerProvider entityListenerProvider,
			DomaConfigBuilder domaConfigBuilder, DomaProperties domaProperties) {
		if (domaConfigBuilder.dataSource() == null) {
			domaConfigBuilder.dataSource(dataSource);
		}
		if (domaConfigBuilder.dialect() == null) {
			domaConfigBuilder.dialect(dialect);
		}
		if (domaConfigBuilder.sqlFileRepository() == null) {
			domaConfigBuilder.sqlFileRepository(sqlFileRepository);
		}
		if (domaConfigBuilder.naming() == null) {
			domaConfigBuilder.naming(naming);
		}
		if (domaConfigBuilder.entityListenerProvider() == null) {
			domaConfigBuilder.entityListenerProvider(entityListenerProvider);
		}
		return domaConfigBuilder.build(domaProperties);
	}

}
