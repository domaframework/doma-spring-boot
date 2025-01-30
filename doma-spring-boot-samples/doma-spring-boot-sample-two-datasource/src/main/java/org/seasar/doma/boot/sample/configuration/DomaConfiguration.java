package org.seasar.doma.boot.sample.configuration;

import javax.sql.DataSource;

import org.seasar.doma.boot.autoconfigure.DomaConfigBuilder;
import org.seasar.doma.boot.autoconfigure.DomaProperties;
import org.seasar.doma.boot.sample.annotation.Secondary;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.DuplicateColumnHandler;
import org.seasar.doma.jdbc.EntityListenerProvider;
import org.seasar.doma.jdbc.JdbcLogger;
import org.seasar.doma.jdbc.ScriptFileLoader;
import org.seasar.doma.jdbc.SqlBuilderSettings;
import org.seasar.doma.jdbc.statistic.StatisticManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DomaConfiguration {

	@Primary
	@Bean
	@ConfigurationProperties("doma")
	public DomaProperties domaProperties() {
		return new DomaProperties();
	}

	@Secondary
	@Bean
	@ConfigurationProperties("secondary.doma")
	public DomaProperties secondaryDomaProperties() {
		return new DomaProperties();
	}

	@Primary
	@Bean
	public DomaConfigBuilder domaConfigBuilder(DomaProperties properties) {
		return properties.initializeDomaConfigBuilder();
	}

	@Secondary
	@Bean
	public DomaConfigBuilder secondaryDomaConfigBuilder(@Secondary DomaProperties properties) {
		return properties.initializeDomaConfigBuilder();
	}

	@Primary
	@Bean
	public Config config(DomaConfigBuilder domaConfigBuilder, DataSource dataSource,
			JdbcLogger jdbcLogger, EntityListenerProvider entityListenerProvider,
			DuplicateColumnHandler duplicateColumnHandler, ScriptFileLoader scriptFileLoader,
			SqlBuilderSettings sqlBuilderSettings, StatisticManager statisticManager) {
		return domaConfigBuilder
				.dataSource(dataSource)
				.jdbcLogger(jdbcLogger)
				.entityListenerProvider(entityListenerProvider)
				.duplicateColumnHandler(duplicateColumnHandler)
				.scriptFileLoader(scriptFileLoader)
				.sqlBuilderSettings(sqlBuilderSettings)
				.statisticManager(statisticManager)
				.build();
	}

	@Secondary
	@Bean
	public Config secondaryConfig(@Secondary DomaConfigBuilder domaConfigBuilder,
			@Secondary DataSource dataSource, JdbcLogger jdbcLogger,
			EntityListenerProvider entityListenerProvider,
			DuplicateColumnHandler duplicateColumnHandler, ScriptFileLoader scriptFileLoader,
			SqlBuilderSettings sqlBuilderSettings, StatisticManager statisticManager) {
		return domaConfigBuilder
				.dataSource(dataSource)
				.jdbcLogger(jdbcLogger)
				.entityListenerProvider(entityListenerProvider)
				.duplicateColumnHandler(duplicateColumnHandler)
				.scriptFileLoader(scriptFileLoader)
				.sqlBuilderSettings(sqlBuilderSettings)
				.statisticManager(statisticManager)
				.build();
	}
}
