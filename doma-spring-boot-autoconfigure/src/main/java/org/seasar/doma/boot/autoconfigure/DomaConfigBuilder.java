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

import javax.sql.DataSource;

import org.seasar.doma.jdbc.*;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

/**
 * Builder to create {@link DomaConfig}.
 *
 * @author Toshiaki Maki
 */
public class DomaConfigBuilder {
	private DomaProperties domaProperties;
	private DataSource dataSource;
	/**
	 * Default value is set in {@link DomaProperties}
	 */
	private Dialect dialect;
	/**
	 * Default value is set in {@link DomaProperties}
	 */
	private JdbcLogger jdbcLogger;
	/**
	 * Default value is set in {@link DomaProperties}
	 */
	private SqlFileRepository sqlFileRepository;
	private RequiresNewController requiresNewController = ConfigSupport.defaultRequiresNewController;
	private ClassHelper classHelper = ConfigSupport.defaultClassHelper;
	private CommandImplementors commandImplementors = ConfigSupport.defaultCommandImplementors;
	private QueryImplementors queryImplementors = ConfigSupport.defaultQueryImplementors;
	private UnknownColumnHandler unknownColumnHandler = ConfigSupport.defaultUnknownColumnHandler;
	/**
	 * Default value is set in {@link DomaProperties}
	 */
	private Naming naming;
	private MapKeyNaming mapKeyNaming = ConfigSupport.defaultMapKeyNaming;
	private Commenter commenter = ConfigSupport.defaultCommenter;
	private EntityListenerProvider entityListenerProvider;

	@Deprecated
	public DomaConfigBuilder() {
	}

	public DomaConfigBuilder(DomaProperties domaProperties) {
		this.domaProperties = domaProperties;
	}

	DomaProperties domaProperties() {
		return domaProperties;
	}

	public DataSource dataSource() {
		return dataSource;
	}

	/**
	 * Set dataSource<br>
	 * <p>
	 * Note that the given dataSource is wrapped by
	 * {@link TransactionAwareDataSourceProxy}.
	 *
	 * @param dataSource dataSource to use
	 * @return chained builder
	 */
	public DomaConfigBuilder dataSource(DataSource dataSource) {
		this.dataSource = new TransactionAwareDataSourceProxy(dataSource);
		return this;
	}

	public Dialect dialect() {
		return dialect;
	}

	public DomaConfigBuilder dialect(Dialect dialect) {
		this.dialect = dialect;
		return this;
	}

	public JdbcLogger jdbcLogger() {
		return jdbcLogger;
	}

	public DomaConfigBuilder jdbcLogger(JdbcLogger jdbcLogger) {
		this.jdbcLogger = jdbcLogger;
		return this;
	}

	public SqlFileRepository sqlFileRepository() {
		return sqlFileRepository;
	}

	public DomaConfigBuilder sqlFileRepository(SqlFileRepository sqlFileRepository) {
		this.sqlFileRepository = sqlFileRepository;
		return this;
	}

	public RequiresNewController requiresNewController() {
		return requiresNewController;
	}

	public DomaConfigBuilder requiresNewController(
			RequiresNewController requiresNewController) {
		this.requiresNewController = requiresNewController;
		return this;
	}

	public ClassHelper classHelper() {
		return classHelper;
	}

	public DomaConfigBuilder classHelper(ClassHelper classHelper) {
		this.classHelper = classHelper;
		return this;
	}

	public CommandImplementors commandImplementors() {
		return commandImplementors;
	}

	public DomaConfigBuilder commandImplementors(CommandImplementors commandImplementors) {
		this.commandImplementors = commandImplementors;
		return this;
	}

	public QueryImplementors queryImplementors() {
		return queryImplementors;
	}

	public DomaConfigBuilder queryImplementors(QueryImplementors queryImplementors) {
		this.queryImplementors = queryImplementors;
		return this;
	}

	public UnknownColumnHandler unknownColumnHandler() {
		return unknownColumnHandler;
	}

	public DomaConfigBuilder unknownColumnHandler(
			UnknownColumnHandler unknownColumnHandler) {
		this.unknownColumnHandler = unknownColumnHandler;
		return this;
	}

	public Naming naming() {
		return naming;
	}

	public DomaConfigBuilder naming(Naming naming) {
		this.naming = naming;
		return this;
	}

	public MapKeyNaming mapKeyNaming() {
		return mapKeyNaming;
	}

	public DomaConfigBuilder mapKeyNaming(MapKeyNaming mapKeyNaming) {
		this.mapKeyNaming = mapKeyNaming;
		return this;
	}

	public Commenter commenter() {
		return commenter;
	}

	public DomaConfigBuilder commenter(Commenter commenter) {
		this.commenter = commenter;
		return this;
	}

	public EntityListenerProvider entityListenerProvider() {
		return entityListenerProvider;
	}

	public DomaConfigBuilder entityListenerProvider(
			EntityListenerProvider entityListenerProvider) {
		this.entityListenerProvider = entityListenerProvider;
		return this;
	}

	@Deprecated
	public DomaConfig build(DomaProperties domaProperties) {
		this.domaProperties = domaProperties;
		return build();
	}

	public DomaConfig build() {
		return new DomaConfig(this, domaProperties);
	}
}
