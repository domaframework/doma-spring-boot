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

import org.seasar.doma.jdbc.*;
import org.seasar.doma.jdbc.dialect.Dialect;

import javax.sql.DataSource;

/**
 * {@link Config} implementation used in doma-spring-boot.
 *
 * @author Toshiaki Maki
 */
public class DomaConfig implements Config {
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

	private final DataSource dataSource;
	private final Dialect dialect;
	private final JdbcLogger jdbcLogger;
	private final SqlFileRepository sqlFileRepository;
	private final RequiresNewController requiresNewController;
	private final ClassHelper classHelper;
	private final CommandImplementors commandImplementors;
	private final QueryImplementors queryImplementors;
	private final UnknownColumnHandler unknownColumnHandler;
	private final Naming naming;
	private final MapKeyNaming mapKeyNaming;
	private final Commenter commenter;
	private final EntityListenerProvider entityListenerProvider;

	public DomaConfig(DomaConfigBuilder builder) {
		this.dataSource = builder.dataSource();
		this.dialect = builder.dialect();
		this.jdbcLogger = builder.jdbcLogger();
		this.sqlFileRepository = builder.sqlFileRepository();
		this.requiresNewController = builder.requiresNewController();
		this.classHelper = builder.classHelper();
		this.commandImplementors = builder.commandImplementors();
		this.queryImplementors = builder.queryImplementors();
		this.unknownColumnHandler = builder.unknownColumnHandler();
		this.naming = builder.naming();
		this.mapKeyNaming = builder.mapKeyNaming();
		this.commenter = builder.commenter();
		this.entityListenerProvider = builder.entityListenerProvider();
	}

	@Override
	public DataSource getDataSource() {
		return this.dataSource;
	}

	@Override
	public Dialect getDialect() {
		return this.dialect;
	}

	@Override
	public String getDataSourceName() {
		return this.dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	@Override
	public SqlFileRepository getSqlFileRepository() {
		return this.sqlFileRepository;
	}

	@Override
	public JdbcLogger getJdbcLogger() {
		return this.jdbcLogger;
	}

	@Override
	public RequiresNewController getRequiresNewController() {
		return this.requiresNewController;
	}

	@Override
	public ClassHelper getClassHelper() {
		return this.classHelper;
	}

	@Override
	public CommandImplementors getCommandImplementors() {
		return this.commandImplementors;
	}

	@Override
	public QueryImplementors getQueryImplementors() {
		return this.queryImplementors;
	}

	public void setExceptionSqlLogType(SqlLogType exceptionSqlLogType) {
		this.exceptionSqlLogType = exceptionSqlLogType;
	}

	@Override
	public SqlLogType getExceptionSqlLogType() {
		return this.exceptionSqlLogType;
	}

	@Override
	public UnknownColumnHandler getUnknownColumnHandler() {
		return this.unknownColumnHandler;
	}

	@Override
	public Naming getNaming() {
		return this.naming;
	}

	@Override
	public MapKeyNaming getMapKeyNaming() {
		return this.mapKeyNaming;
	}

	@Override
	public Commenter getCommenter() {
		return this.commenter;
	}

	@Override
	public int getMaxRows() {
		return this.maxRows;
	}

	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	@Override
	public int getFetchSize() {
		return this.fetchSize;
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	@Override
	public int getQueryTimeout() {
		return this.queryTimeout;
	}

	public void setQueryTimeout(int queryTimeout) {
		this.queryTimeout = queryTimeout;
	}

	@Override
	public int getBatchSize() {
		return this.batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	@Override
	public EntityListenerProvider getEntityListenerProvider() {
		return this.entityListenerProvider;
	}
}
