package org.seasar.doma.boot.sample.configuration;

import javax.sql.DataSource;

import org.seasar.doma.boot.sample.annotation.Secondary;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfiguration {

	@Primary
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSourceProperties dataSourceProperties() {
		return new DataSourceProperties();
	}

	@Secondary
	@Bean
	@ConfigurationProperties(prefix = "secondary.datasource")
	public DataSourceProperties secondaryDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Primary
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.hikari")
	public HikariDataSource dataSource(DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	@Secondary
	@Bean
	@ConfigurationProperties(prefix = "secondary.datasource.hikari")
	public HikariDataSource secondaryDataSource(@Secondary DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	@Primary
	@Bean
	@ConfigurationProperties(prefix = "spring.sql.init")
	public SqlInitializationProperties sqlInitializationProperties() {
		return new SqlInitializationProperties();
	}

	@Secondary
	@Bean
	@ConfigurationProperties(prefix = "secondary.sql.init")
	public SqlInitializationProperties secondarySqlInitializationProperties() {
		return new SqlInitializationProperties();
	}

	@Primary
	@Bean
	public SqlDataSourceScriptDatabaseInitializer dataSourceInitializer(DataSource dataSource,
			SqlInitializationProperties properties) {
		return new SqlDataSourceScriptDatabaseInitializer(dataSource, properties);
	}

	@Secondary
	@Bean
	public SqlDataSourceScriptDatabaseInitializer secondaryDataSourceInitializer(@Secondary DataSource dataSource,
			@Secondary SqlInitializationProperties properties) {
		return new SqlDataSourceScriptDatabaseInitializer(dataSource, properties);
	}
}
