package org.seasar.doma.boot.sample.configuration;

import java.util.List;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.seasar.doma.boot.sample.annotation.Secondary;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

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

	@Secondary
	@Bean
	public DataSourceInitializer secondaryDataSourceInitializer(@Secondary DataSource dataSource,
			@Secondary DataSourceProperties properties, ResourceLoader resourceLoader) {

		Resource[] scripts = Stream.of(properties.getSchema(), properties.getData())
				.flatMap(List::stream)
				.map(resourceLoader::getResource)
				.toArray(Resource[]::new);

		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(scripts);

		DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
		dataSourceInitializer.setDataSource(dataSource);
		dataSourceInitializer.setDatabasePopulator(databasePopulator);
		dataSourceInitializer.setEnabled(scripts.length > 0);
		return dataSourceInitializer;
	}
}
