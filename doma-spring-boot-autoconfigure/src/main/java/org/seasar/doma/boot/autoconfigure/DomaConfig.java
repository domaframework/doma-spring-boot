package org.seasar.doma.boot.autoconfigure;

import javax.sql.DataSource;

import org.seasar.doma.jdbc.*;
import org.seasar.doma.jdbc.dialect.Dialect;

/**
 * {@link Config} implementation used in doma-spring-boot.
 *
 * @author Toshiaki Maki
 */
public class DomaConfig implements Config {

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
	private final DomaProperties domaProperties;

	public DomaConfig(DomaConfigBuilder builder, DomaProperties domaProperties) {
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
		this.domaProperties = domaProperties;
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
		return this.domaProperties.getDataSourceName();
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

	@Override
	public SqlLogType getExceptionSqlLogType() {
		return this.domaProperties.getExceptionSqlLogType();
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
		return this.domaProperties.getMaxRows();
	}

	@Override
	public int getFetchSize() {
		return this.domaProperties.getFetchSize();
	}

	@Override
	public int getQueryTimeout() {
		return this.domaProperties.getQueryTimeout();
	}

	@Override
	public int getBatchSize() {
		return this.domaProperties.getBatchSize();
	}

	@Override
	public EntityListenerProvider getEntityListenerProvider() {
		return this.entityListenerProvider;
	}

	@Override
	public String toString() {
		return "DomaConfig{" + "dataSource=" + dataSource + ", dialect=" + dialect
				+ ", jdbcLogger=" + jdbcLogger + ", sqlFileRepository="
				+ sqlFileRepository + ", requiresNewController=" + requiresNewController
				+ ", classHelper=" + classHelper + ", commandImplementors="
				+ commandImplementors + ", queryImplementors=" + queryImplementors
				+ ", unknownColumnHandler=" + unknownColumnHandler + ", naming=" + naming
				+ ", mapKeyNaming=" + mapKeyNaming + ", commenter=" + commenter
				+ ", entityListenerProvider=" + entityListenerProvider
				+ ", domaProperties=" + domaProperties + '}';
	}
}
