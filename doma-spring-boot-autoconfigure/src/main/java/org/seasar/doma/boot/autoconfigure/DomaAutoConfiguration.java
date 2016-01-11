package org.seasar.doma.boot.autoconfigure;

import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.Naming;
import org.seasar.doma.jdbc.SqlFileRepository;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.support.PersistenceExceptionTranslator;

import javax.sql.DataSource;

import static org.seasar.doma.boot.autoconfigure.DomaProperties.DOMA_PREFIX;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration Auto-configuration} for Doma.
 *
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
    public Naming naming() {
        return domaProperties.getNaming().naming();
    }

    @Bean
    @ConditionalOnMissingBean
    public DomaConfigBuilder domaConfigBuilder() {
        return new DomaConfigBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = DOMA_PREFIX)
    public DomaConfig config(DataSource dataSource, Dialect dialect, SqlFileRepository sqlFileRepository, Naming naming,
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
        return new DomaConfig(domaConfigBuilder);
    }

}
