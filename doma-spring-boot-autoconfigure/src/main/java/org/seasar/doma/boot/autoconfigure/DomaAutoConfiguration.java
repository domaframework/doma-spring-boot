package org.seasar.doma.boot.autoconfigure;

import java.util.function.Supplier;

import javax.sql.DataSource;

import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.ConfigSupport;
import org.seasar.doma.jdbc.EntityListenerProvider;
import org.seasar.doma.jdbc.JdbcLogger;
import org.seasar.doma.jdbc.MapKeyNaming;
import org.seasar.doma.jdbc.Naming;
import org.seasar.doma.jdbc.SqlFileRepository;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.entity.EntityListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration Auto-configuration} for Doma.
 * @author Toshiaki Maki
 */
@Configuration
@ConditionalOnClass(Config.class)
@EnableConfigurationProperties(DomaProperties.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class DomaAutoConfiguration {
    @Autowired
    DomaProperties domaProperties;

    @Bean
    @ConditionalOnMissingBean
    public Dialect dialect() {
        return domaProperties.getDialect().create();
    }

    @Bean
    @ConditionalOnProperty(prefix = DomaProperties.DOMA_PREFIX, name = "exception-translation-enabled", matchIfMissing = true)
    public PersistenceExceptionTranslator exceptionTranslator() {
        return new DomaPersistenceExceptionTranslator();
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlFileRepository sqlFileRepository() {
        return domaProperties.getSqlFileRepository().create();
    }

    @Bean
    @ConditionalOnMissingBean
    public JdbcLogger jdbcLogger() {
        return ConfigSupport.defaultJdbcLogger;
    }

    @Bean
    @ConditionalOnMissingBean
    public Naming naming() {
        return domaProperties.getNaming().get();
    }

    @Bean
    @ConditionalOnMissingBean
    public MapKeyNaming mapKeyNaming() {
        return ConfigSupport.defaultMapKeyNaming;
    }

    @Bean
    @ConditionalOnProperty(prefix = DomaProperties.DOMA_PREFIX, name = "entity-listener-provider-bean-factory", havingValue="true", matchIfMissing = false)
    public EntityListenerProvider entityListenerProviderBeanFactory(final ApplicationContext context) {
        return new EntityListenerProvider() {
            @Override
            public <ENTITY, LISTENER extends EntityListener<ENTITY>> LISTENER get(
                    final Class<LISTENER> listenerClass,
                    final Supplier<LISTENER> listenerSupplier) {
                return context.getBean(listenerClass);
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public EntityListenerProvider entityListenerProvider() {
        return ConfigSupport.defaultEntityListenerProvider;
    }

    @Bean
    @ConditionalOnMissingBean
    public Config config(DataSource dataSource, Dialect dialect,
            SqlFileRepository sqlFileRepository, JdbcLogger jdbcLogger,
            EntityListenerProvider entityListenerProvider, Naming naming,
            MapKeyNaming mapKeyNaming) {
        return new Config() {
            @Override
            public DataSource getDataSource() {
                return new TransactionAwareDataSourceProxy(dataSource);
            }

            @Override
            public Dialect getDialect() {
                return dialect;
            }

            @Override
            public SqlFileRepository getSqlFileRepository() {
                return sqlFileRepository;
            }

            @Override
            public JdbcLogger getJdbcLogger() {
                return jdbcLogger;
            }

            @Override
            public Naming getNaming() {
                return naming;
            }

            @Override
            public MapKeyNaming getMapKeyNaming() {
                return mapKeyNaming;
            }

            @Override
            public int getMaxRows() {
                return domaProperties.getMaxRows();
            }

            @Override
            public int getFetchSize() {
                return domaProperties.getFetchSize();
            }

            @Override
            public int getQueryTimeout() {
                return domaProperties.getQueryTimeout();
            }

            @Override
            public int getBatchSize() {
                return domaProperties.getBatchSize();
            }

            @Override
            public EntityListenerProvider getEntityListenerProvider() {
                return entityListenerProvider;
            }
        };
    }

}
