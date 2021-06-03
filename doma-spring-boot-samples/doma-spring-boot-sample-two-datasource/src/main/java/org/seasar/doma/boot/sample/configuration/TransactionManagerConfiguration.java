package org.seasar.doma.boot.sample.configuration;

import javax.sql.DataSource;

import org.seasar.doma.boot.sample.annotation.Secondary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransactionManagerConfiguration {

	@Bean
	public PlatformTransactionManager transactionManager(DataSource dataSource,
			@Secondary DataSource secondaryDataSource) {
		return new ChainedTransactionManager(
				new DataSourceTransactionManager(dataSource),
				new DataSourceTransactionManager(secondaryDataSource));
	}
}
