package org.seasar.doma.boot.autoconfigure;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.doma.boot.DomaPersistenceExceptionTranslator;
import org.seasar.doma.boot.TryLookupEntityListenerProvider;
import org.seasar.doma.boot.autoconfigure.DomaProperties.DialectType;
import org.seasar.doma.boot.event.DomaEventEntityListener;
import org.seasar.doma.boot.event.DomaEventListenerFactory;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.EntityListenerProvider;
import org.seasar.doma.jdbc.JdbcLogger;
import org.seasar.doma.jdbc.Naming;
import org.seasar.doma.jdbc.SqlFileRepository;
import org.seasar.doma.jdbc.criteria.Entityql;
import org.seasar.doma.jdbc.criteria.NativeSql;
import org.seasar.doma.jdbc.dialect.Db2Dialect;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.dialect.H2Dialect;
import org.seasar.doma.jdbc.dialect.HsqldbDialect;
import org.seasar.doma.jdbc.dialect.MssqlDialect;
import org.seasar.doma.jdbc.dialect.MysqlDialect;
import org.seasar.doma.jdbc.dialect.OracleDialect;
import org.seasar.doma.jdbc.dialect.PostgresDialect;
import org.seasar.doma.jdbc.dialect.SqliteDialect;
import org.seasar.doma.jdbc.dialect.StandardDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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

	private static final Log logger = LogFactory.getLog(DomaAutoConfiguration.class);

	@Autowired
	private DomaProperties domaProperties;

	@Bean
	@ConditionalOnMissingBean
	public Dialect dialect(Environment environment) {
		DialectType dialectType = domaProperties.getDialect();
		if (dialectType != null) {
			return dialectType.create();
		}
		String url = environment.getProperty("spring.datasource.url");
		if (url != null) {
			DatabaseDriver databaseDriver = DatabaseDriver.fromJdbcUrl(url);
			switch (databaseDriver) {
			case DB2:
				return new Db2Dialect();
			case H2:
				return new H2Dialect();
			case HSQLDB:
				return new HsqldbDialect();
			case SQLSERVER:
			case JTDS:
				return new MssqlDialect();
			case MYSQL:
				return new MysqlDialect();
			case ORACLE:
				return new OracleDialect();
			case POSTGRESQL:
				return new PostgresDialect();
			case SQLITE:
				return new SqliteDialect();
			default:
				break;
			}
		}
		if (logger.isWarnEnabled()) {
			logger.warn(
					"StandardDialect was selected because no explicit configuration and it is not possible to guess from 'spring.datasource.url property'");
		}
		return new StandardDialect();
	}

	@Bean
	@ConditionalOnProperty(prefix = DomaProperties.DOMA_PREFIX, name = "exception-translation-enabled",
			matchIfMissing = true)
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
	@ConditionalOnMissingBean
	public JdbcLogger jdbcLogger() {
		return domaProperties.getJdbcLogger().create();
	}

	@Bean
	public static DomaEventListenerFactory domaEventListenerFactory() {
		return new DomaEventListenerFactory();
	}

	@SuppressWarnings("rawtypes")
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
		return new DomaConfigBuilder(domaProperties);
	}

	@Bean
	@ConditionalOnMissingBean(Config.class)
	public DomaConfig config(DataSource dataSource, Dialect dialect,
			SqlFileRepository sqlFileRepository, Naming naming, JdbcLogger jdbcLogger,
			EntityListenerProvider entityListenerProvider,
			DomaConfigBuilder domaConfigBuilder) {
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
		if (domaConfigBuilder.jdbcLogger() == null) {
			domaConfigBuilder.jdbcLogger(jdbcLogger);
		}
		if (domaConfigBuilder.entityListenerProvider() == null) {
			domaConfigBuilder.entityListenerProvider(entityListenerProvider);
		}
		return domaConfigBuilder.build();
	}

	@Configuration
	@ConditionalOnClass({ Entityql.class, NativeSql.class })
	public static class CriteriaConfiguration {

		@Bean
		@ConditionalOnMissingBean(Entityql.class)
		public Entityql entityql(Config config) {
			return new Entityql(config);
		}

		@Bean
		@ConditionalOnMissingBean(NativeSql.class)
		public NativeSql nativeSql(Config config) {
			return new NativeSql(config);
		}
	}
}
