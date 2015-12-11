package org.seasar.doma.boot.autoconfigure;

import static org.seasar.doma.boot.autoconfigure.DomaProperties.DOMA_PREFIX;

import java.util.function.Supplier;

import org.seasar.doma.jdbc.GreedyCacheSqlFileRepository;
import org.seasar.doma.jdbc.Naming;
import org.seasar.doma.jdbc.NoCacheSqlFileRepository;
import org.seasar.doma.jdbc.SqlFileRepository;
import org.seasar.doma.jdbc.dialect.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * {@link ConfigurationProperties} for configuring Doma.
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
     * Max Rows
     */
    private int maxRows = 0;

    /**
     * Fetch Size
     */
    private int fetchSize = 0;

    /**
     * Query Timeout
     */
    private int queryTimeout = 0;

    /**
     * Batch Size
     */
    private int batchSize = 0;

    /**
     * Whether convert {@link org.seasar.doma.jdbc.JdbcException} into {@link org.springframework.dao.DataAccessException}.
     */
    private boolean exceptionTranslationEnabled = true;

    /**
     * Get {@link org.seasar.doma.jdbc.entity.EntityListener} from {@link org.springframework.beans.factory.BeanFactory}
     */
    private boolean entityListenerProviderBeanFactory = false;

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

    public boolean isExceptionTranslationEnabled() {
        return exceptionTranslationEnabled;
    }

    public void setExceptionTranslationEnabled(
            boolean exceptionTranslationEnabled) {
        this.exceptionTranslationEnabled = exceptionTranslationEnabled;
    }

    public boolean isEntityListenerProviderBeanFactory() {
        return entityListenerProviderBeanFactory;
    }

    public void setEntityListenerProviderBeanFactory(
            boolean entityListenerProviderBeanFactory) {
        this.entityListenerProviderBeanFactory = entityListenerProviderBeanFactory;
    }

    public static enum DialectType {
        STANDARD(StandardDialect::new), SQLITE(SqliteDialect::new), DB2(
                Db2Dialect::new), MSSQL(MssqlDialect::new), MYSQL(
                        MysqlDialect::new), POSTGRES(
                                PostgresDialect::new), ORACLE(
                                        OracleDialect::new), H2(
                                                H2Dialect::new), HSQL(
                                                        HsqldbDialect::new);

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
        DEFAULT(Naming.DEFAULT),
        LENIENT_SNAKE_LOWER_CASE(Naming.LENIENT_SNAKE_LOWER_CASE),
        LENIENT_SNAKE_UPPER_CASE(Naming.LENIENT_SNAKE_UPPER_CASE),
        LOWER_CASE(Naming.LOWER_CASE),
        NONE(Naming.NONE),
        SNAKE_LOWER_CASE(Naming.SNAKE_LOWER_CASE),
        SNAKE_UPPER_CASE(Naming.SNAKE_UPPER_CASE),
        UPPER_CASE(Naming.UPPER_CASE);
        private final Naming naming;

        NamingType(Naming naming) {
            this.naming = naming;
        }

        public Naming get() {
            return this.naming;
        }
    }

    @Override
    public String toString() {
        return "DomaProperties{" + "dialect=" + dialect + ", sqlFileRepository="
                + sqlFileRepository + ", naming=" + naming + ", maxRows=" + maxRows
                + ", fetchSize=" + fetchSize + ", queryTimeout=" + queryTimeout
                + ", batchSize=" + batchSize + ", exceptionTranslationEnabled="
                + exceptionTranslationEnabled + ", entityListenerProviderBeanFactory="
                + entityListenerProviderBeanFactory + '}';
    }
}
