package org.seasar.doma.boot.autoconfigure;

import static org.seasar.doma.boot.autoconfigure.DomaProperties.DOMA_PREFIX;

import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.GreedyCacheSqlFileRepository;
import org.seasar.doma.jdbc.JdbcLogger;
import org.seasar.doma.jdbc.Naming;
import org.seasar.doma.jdbc.NoCacheSqlFileRepository;
import org.seasar.doma.jdbc.SqlFileRepository;
import org.seasar.doma.jdbc.SqlLogType;
import org.seasar.doma.jdbc.UtilLoggingJdbcLogger;
import org.seasar.doma.jdbc.dialect.Db2Dialect;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.dialect.H212126Dialect;
import org.seasar.doma.jdbc.dialect.H214199Dialect;
import org.seasar.doma.jdbc.dialect.H2Dialect;
import org.seasar.doma.jdbc.dialect.HsqldbDialect;
import org.seasar.doma.jdbc.dialect.Mssql2008Dialect;
import org.seasar.doma.jdbc.dialect.MssqlDialect;
import org.seasar.doma.jdbc.dialect.MysqlDialect;
import org.seasar.doma.jdbc.dialect.MysqlDialect.MySqlVersion;
import org.seasar.doma.jdbc.dialect.Oracle11Dialect;
import org.seasar.doma.jdbc.dialect.OracleDialect;
import org.seasar.doma.jdbc.dialect.PostgresDialect;
import org.seasar.doma.jdbc.dialect.SqliteDialect;
import org.seasar.doma.jdbc.dialect.StandardDialect;
import org.seasar.doma.slf4j.Slf4jJdbcLogger;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * {@link ConfigurationProperties} for configuring Doma.
 *
 * @author Toshiaki Maki
 */
@ConfigurationProperties(prefix = DOMA_PREFIX)
public class DomaProperties {
	public static final String DOMA_PREFIX = "doma";

	/**
	 * Dialect of database used by Doma.
	 */
	private DialectType dialect;

	/**
	 * Type of {@link SqlFileRepository}.
	 */
	private SqlFileRepositoryType sqlFileRepository = SqlFileRepositoryType.GREEDY_CACHE;

	/**
	 * Type of {@link Naming}.
	 */
	private NamingType naming = NamingType.DEFAULT;

	/**
	 * Whether convert {@link org.seasar.doma.jdbc.JdbcException} into
	 * {@link org.springframework.dao.DataAccessException}.
	 */
	private boolean exceptionTranslationEnabled = true;

	/**
	 * Datasource name.
	 */
	private String dataSourceName = Config.class.getName();

	/**
	 * Type of SQL log in the exception.
	 */
	private SqlLogType exceptionSqlLogType = SqlLogType.NONE;

	/**
	 * Type of {@link JdbcLogger}.
	 */
	private JdbcLoggerType jdbcLogger = JdbcLoggerType.JUL;

	/**
	 * Limit for the maximum number of rows. Ignored unless this value is greater than 0.
	 */
	private int maxRows = 0;
	/**
	 * Hint to the number of rows that should be fetched. Ignored unless this value is
	 * greater than 0.
	 */
	private int fetchSize = 0;
	/**
	 * Number of seconds the driver will wait for a <code>Statement</code> object to
	 * execute. Ignored unless this value is greater than 0.
	 */
	private int queryTimeout = 0;
	/**
	 * Size in executing <code>PreparedStatement#addBatch()</code>. Regarded as 1 unless
	 * this value is greater than 1.
	 */
	private int batchSize = 0;

	public DialectType getDialect() {
		return dialect;
	}

	public void setDialect(DialectType dialect) {
		this.dialect = dialect;
	}

	public SqlFileRepositoryType getSqlFileRepository() {
		return sqlFileRepository;
	}

	public void setSqlFileRepository(SqlFileRepositoryType sqlFileRepository) {
		this.sqlFileRepository = sqlFileRepository;
	}

	public NamingType getNaming() {
		return naming;
	}

	public void setNaming(NamingType naming) {
		this.naming = naming;
	}

	public boolean isExceptionTranslationEnabled() {
		return exceptionTranslationEnabled;
	}

	public void setExceptionTranslationEnabled(boolean exceptionTranslationEnabled) {
		this.exceptionTranslationEnabled = exceptionTranslationEnabled;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public SqlLogType getExceptionSqlLogType() {
		return exceptionSqlLogType;
	}

	public void setExceptionSqlLogType(SqlLogType exceptionSqlLogType) {
		this.exceptionSqlLogType = exceptionSqlLogType;
	}

	public JdbcLoggerType getJdbcLogger() {
		return jdbcLogger;
	}

	public void setJdbcLogger(JdbcLoggerType jdbcLogger) {
		this.jdbcLogger = jdbcLogger;
	}

	public int getMaxRows() {
		return maxRows;
	}

	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	public int getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	public int getQueryTimeout() {
		return queryTimeout;
	}

	public void setQueryTimeout(int queryTimeout) {
		this.queryTimeout = queryTimeout;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public DomaConfigBuilder initializeDomaConfigBuilder() {
		return new DomaConfigBuilder(this).dialect(dialect.create())
				.sqlFileRepository(sqlFileRepository.create()).naming(naming.naming());
	}

	public enum DialectType {
		STANDARD(StandardDialect::new),
		SQLITE(SqliteDialect::new),
		DB2(Db2Dialect::new),
		MSSQL(MssqlDialect::new),
		MSSQL2008(Mssql2008Dialect::new),
		MYSQL(MysqlDialect::new),
		MYSQLV5(() -> new MysqlDialect(MySqlVersion.V5)),
		MYSQLV8(() -> new MysqlDialect(MySqlVersion.V8)),
		POSTGRES(PostgresDialect::new),
		ORACLE11(Oracle11Dialect::new),
		ORACLE(OracleDialect::new),
		H2(H2Dialect::new),
		H212126(H212126Dialect::new),
		H214199(H214199Dialect::new),
		HSQL(HsqldbDialect::new);

		private final Supplier<Dialect> constructor;

		DialectType(Supplier<Dialect> constructor) {
			this.constructor = constructor;
		}

		public Dialect create() {
			return this.constructor.get();
		}
	}

	public enum SqlFileRepositoryType {
		NO_CACHE(NoCacheSqlFileRepository::new),
		GREEDY_CACHE(GreedyCacheSqlFileRepository::new);

		private final Supplier<SqlFileRepository> constructor;

		SqlFileRepositoryType(Supplier<SqlFileRepository> constructor) {
			this.constructor = constructor;
		}

		public SqlFileRepository create() {
			return this.constructor.get();
		}
	}

	public enum NamingType {
		NONE(Naming.NONE),
		LOWER_CASE(Naming.LOWER_CASE),
		UPPER_CASE(Naming.UPPER_CASE),
		SNAKE_LOWER_CASE(Naming.SNAKE_LOWER_CASE),
		SNAKE_UPPER_CASE(Naming.SNAKE_UPPER_CASE),
		DEFAULT(Naming.DEFAULT);

		private final Naming naming;

		NamingType(Naming naming) {
			this.naming = naming;
		}

		public Naming naming() {
			return this.naming;
		}
	}

	public enum JdbcLoggerType {
		JUL(UtilLoggingJdbcLogger::new),
		SLF4J(JdbcLoggerType::slf4jJdbcLogger);

		private static JdbcLogger slf4jJdbcLogger() {
			try {
				return new Slf4jJdbcLogger();
			} catch (NoClassDefFoundError e) {
				Log logger = LogFactory.getLog(JdbcLoggerType.class);
				logger.info(
						"org.seasar.doma.slf4j.Slf4jJdbcLogger is not found, fallback to org.seasar.doma.jdbc.Slf4jJdbcLogger");
				try {
					return (JdbcLogger) Class.forName("org.seasar.doma.jdbc.Slf4jJdbcLogger")
							.getConstructor().newInstance();
				} catch (ReflectiveOperationException roe) {
					logger.warn("org.seasar.doma.jdbc.Slf4jJdbcLogger could not be instantiated either.", roe);
				}
				throw e;
			}
		}

		private final Supplier<JdbcLogger> constructor;

		JdbcLoggerType(Supplier<JdbcLogger> constructor) {
			this.constructor = constructor;
		}

		public JdbcLogger create() {
			return this.constructor.get();
		}
	}

	@Override
	public String toString() {
		return "DomaProperties{" + "dialect=" + dialect + ", sqlFileRepository="
				+ sqlFileRepository + ", naming=" + naming
				+ ", exceptionTranslationEnabled=" + exceptionTranslationEnabled
				+ ", dataSourceName='" + dataSourceName + '\'' + ", exceptionSqlLogType="
				+ exceptionSqlLogType + ", jdbcLogger="
				+ jdbcLogger + ", maxRows=" + maxRows + ", fetchSize="
				+ fetchSize + ", queryTimeout=" + queryTimeout + ", batchSize="
				+ batchSize + '}';
	}
}
