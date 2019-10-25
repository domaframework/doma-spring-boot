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

import static org.seasar.doma.boot.autoconfigure.DomaProperties.DOMA_PREFIX;

import java.util.function.Supplier;

import org.seasar.doma.jdbc.*;
import org.seasar.doma.jdbc.dialect.*;
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
	private DialectType dialect = DialectType.STANDARD;

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

	public static enum DialectType {
		STANDARD(StandardDialect::new), SQLITE(SqliteDialect::new), DB2(Db2Dialect::new), MSSQL(
				MssqlDialect::new), MSSQL2008(Mssql2008Dialect::new), MYSQL(
				MysqlDialect::new), POSTGRES(PostgresDialect::new), ORACLE(
				OracleDialect::new), H2(H2Dialect::new), HSQL(HsqldbDialect::new);

		private final Supplier<Dialect> constructor;

		DialectType(Supplier<Dialect> constructor) {
			this.constructor = constructor;
		}

		public Dialect create() {
			return this.constructor.get();
		}
	}

	public static enum SqlFileRepositoryType {
		NO_CACHE(NoCacheSqlFileRepository::new), GREEDY_CACHE(
				GreedyCacheSqlFileRepository::new);

		private final Supplier<SqlFileRepository> constructor;

		SqlFileRepositoryType(Supplier<SqlFileRepository> constructor) {
			this.constructor = constructor;
		}

		public SqlFileRepository create() {
			return this.constructor.get();
		}
	}

	public static enum NamingType {
		NONE(Naming.NONE), LOWER_CASE(Naming.LOWER_CASE), UPPER_CASE(Naming.UPPER_CASE), SNAKE_LOWER_CASE(
				Naming.SNAKE_LOWER_CASE), SNAKE_UPPER_CASE(Naming.SNAKE_UPPER_CASE), LENIENT_SNAKE_LOWER_CASE(
				Naming.LENIENT_SNAKE_LOWER_CASE), LENIENT_SNAKE_UPPER_CASE(
				Naming.LENIENT_SNAKE_UPPER_CASE), DEFAULT(Naming.DEFAULT);

		private final Naming naming;

		NamingType(Naming naming) {
			this.naming = naming;
		}

		public Naming naming() {
			return this.naming;
		}
	}

	@Override
	public String toString() {
		return "DomaProperties{" + "dialect=" + dialect + ", sqlFileRepository="
				+ sqlFileRepository + ", naming=" + naming
				+ ", exceptionTranslationEnabled=" + exceptionTranslationEnabled
				+ ", dataSourceName='" + dataSourceName + '\'' + ", exceptionSqlLogType="
				+ exceptionSqlLogType + ", maxRows=" + maxRows + ", fetchSize="
				+ fetchSize + ", queryTimeout=" + queryTimeout + ", batchSize="
				+ batchSize + '}';
	}
}
