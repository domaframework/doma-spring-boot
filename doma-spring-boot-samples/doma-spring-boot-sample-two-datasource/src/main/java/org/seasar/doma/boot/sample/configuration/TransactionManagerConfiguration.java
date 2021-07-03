package org.seasar.doma.boot.sample.configuration;

import javax.sql.DataSource;

import org.seasar.doma.boot.sample.annotation.Secondary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class TransactionManagerConfiguration {

	@Bean
	public DataSourceTransactionManager primaryTransactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	public DataSourceTransactionManager secondaryTransactionManager(
			@Secondary DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}
