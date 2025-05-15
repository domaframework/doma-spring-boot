package org.seasar.doma.boot.autoconfigure;

import javax.sql.DataSource;

import org.seasar.doma.jdbc.*;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.statistic.StatisticManager;

/**
 * Implementation of {@link Config} used by the Doma Spring Boot integration.
 * <p>
 * This class encapsulates all configuration properties required by Doma,
 * such as {@link DataSource}, {@link Dialect}, and {@link JdbcLogger}.
 * <p>
 * The configuration is built using a builder pattern through {@link DomaConfigBuilder}.
 *
 * @author Toshiaki Maki
 * @see DomaConfigBuilder
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
	private final DuplicateColumnHandler duplicateColumnHandler;
	private final ScriptFileLoader scriptFileLoader;
	private final SqlBuilderSettings sqlBuilderSettings;
	private final StatisticManager statisticManager;

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
		this.duplicateColumnHandler = builder.duplicateColumnHandler();
		this.scriptFileLoader = builder.scriptFileLoader();
		this.sqlBuilderSettings = builder.sqlBuilderSettings();
		this.statisticManager = builder.statisticManager();
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
	public DuplicateColumnHandler getDuplicateColumnHandler() {
		return this.duplicateColumnHandler;
	}

	@Override
	public ScriptFileLoader getScriptFileLoader() {
		return this.scriptFileLoader;
	}

	@Override
	public SqlBuilderSettings getSqlBuilderSettings() {
		return this.sqlBuilderSettings;
	}

	@Override
	public StatisticManager getStatisticManager() {
		return this.statisticManager;
	}
}
