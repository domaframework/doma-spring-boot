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

import org.seasar.doma.jdbc.GreedyCacheSqlFileRepository;
import org.seasar.doma.jdbc.Naming;
import org.seasar.doma.jdbc.NoCacheSqlFileRepository;
import org.seasar.doma.jdbc.SqlFileRepository;
import org.seasar.doma.jdbc.dialect.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.function.Supplier;

import static org.seasar.doma.boot.autoconfigure.DomaProperties.DOMA_PREFIX;

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
				+ ", exceptionTranslationEnabled=" + exceptionTranslationEnabled + '}';
	}
}
