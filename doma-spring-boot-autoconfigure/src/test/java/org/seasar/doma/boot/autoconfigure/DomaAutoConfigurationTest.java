package org.seasar.doma.boot.autoconfigure;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.Map;
import java.util.function.Predicate;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.seasar.doma.boot.DomaPersistenceExceptionTranslator;
import org.seasar.doma.boot.DomaSpringBootSqlBuilderSettings;
import org.seasar.doma.boot.ResourceLoaderScriptFileLoader;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.ConfigSupport;
import org.seasar.doma.jdbc.DuplicateColumnHandler;
import org.seasar.doma.jdbc.EntityListenerProvider;
import org.seasar.doma.jdbc.GreedyCacheSqlFileRepository;
import org.seasar.doma.jdbc.JdbcException;
import org.seasar.doma.jdbc.JdbcLogger;
import org.seasar.doma.jdbc.Naming;
import org.seasar.doma.jdbc.NoCacheSqlFileRepository;
import org.seasar.doma.jdbc.ScriptFileLoader;
import org.seasar.doma.jdbc.SqlBuilderSettings;
import org.seasar.doma.jdbc.SqlFileRepository;
import org.seasar.doma.jdbc.ThrowingDuplicateColumnHandler;
import org.seasar.doma.jdbc.UtilLoggingJdbcLogger;
import org.seasar.doma.jdbc.criteria.Entityql;
import org.seasar.doma.jdbc.criteria.NativeSql;
import org.seasar.doma.jdbc.criteria.QueryDsl;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.dialect.MysqlDialect;
import org.seasar.doma.jdbc.dialect.PostgresDialect;
import org.seasar.doma.jdbc.dialect.StandardDialect;
import org.seasar.doma.jdbc.statistic.DefaultStatisticManager;
import org.seasar.doma.jdbc.statistic.StatisticManager;
import org.seasar.doma.message.Message;
import org.seasar.doma.slf4j.Slf4jJdbcLogger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

class DomaAutoConfigurationTest {
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(DomaAutoConfiguration.class,
					DataSourceAutoConfiguration.class));

	@Test
	void autoRegisteredConfig() {
		this.contextRunner
				.run(context -> {
					Config config = context.getBean(Config.class);
					assertThat(config, is(notNullValue()));
					assertThat(config.getDataSource(),
							is(instanceOf(TransactionAwareDataSourceProxy.class)));
					assertThat(config.getDialect(), is(instanceOf(StandardDialect.class)));
					assertThat(config.getSqlFileRepository(),
							is(instanceOf(GreedyCacheSqlFileRepository.class)));
					assertThat(config.getNaming(), is(Naming.DEFAULT));
					assertThat(config.getJdbcLogger(), is(instanceOf(Slf4jJdbcLogger.class)));
					assertThat(config.getEntityListenerProvider(), is(notNullValue()));
					assertThat(config.getDuplicateColumnHandler(),
							is(ConfigSupport.defaultDuplicateColumnHandler));
					assertThat(config.getScriptFileLoader(),
							is(instanceOf(ResourceLoaderScriptFileLoader.class)));
					assertThat(config.getSqlBuilderSettings(),
							is(instanceOf(DomaSpringBootSqlBuilderSettings.class)));
					assertThat(config.getStatisticManager(),
							is(instanceOf(DefaultStatisticManager.class)));
					PersistenceExceptionTranslator translator = context
							.getBean(PersistenceExceptionTranslator.class);
					assertThat(translator,
							is(instanceOf(DomaPersistenceExceptionTranslator.class)));
				});
	}

	@Test
	void configWithDomaConfigBuilder() {
		this.contextRunner
				.withUserConfiguration(ConfigBuilderConfigure.class)
				.run(context -> {
					Config config = context.getBean(Config.class);
					assertThat(config, is(notNullValue()));
					assertThat(config.getDataSource(),
							is(instanceOf(TransactionAwareDataSourceProxy.class)));
					assertThat(config.getDialect(), is(instanceOf(MysqlDialect.class)));
					assertThat(config.getSqlFileRepository(),
							is(instanceOf(NoCacheSqlFileRepository.class)));
					assertThat(config.getNaming(), is(Naming.SNAKE_UPPER_CASE));
					assertThat(config.getJdbcLogger(), is(instanceOf(UtilLoggingJdbcLogger.class)));
					assertThat(config.getEntityListenerProvider(),
							is(instanceOf(TestEntityListenerProvider.class)));
					assertThat(config.getDuplicateColumnHandler(),
							is(ConfigBuilderConfigure.testDuplicateColumnHandler));
					assertThat(config.getScriptFileLoader(),
							is(ConfigBuilderConfigure.testScriptFileLoader));
					assertThat(config.getSqlBuilderSettings(),
							is(ConfigBuilderConfigure.testSqlBuilderSettings));
					assertThat(config.getStatisticManager(),
							is(ConfigBuilderConfigure.testStatisticManager));
					PersistenceExceptionTranslator translator = context
							.getBean(PersistenceExceptionTranslator.class);
					assertThat(translator,
							is(instanceOf(DomaPersistenceExceptionTranslator.class)));
				});
	}

	@Test
	void configWithConfig() {
		this.contextRunner
				.withUserConfiguration(ConfigConfigure.class)
				.run(context -> {
					Config config = context.getBean(Config.class);
					assertThat(config, is(notNullValue()));
					assertThat(config.getDataSource(),
							is(instanceOf(TransactionAwareDataSourceProxy.class)));
					assertThat(config.getDialect(), is(instanceOf(PostgresDialect.class)));
					assertThat(config.getSqlFileRepository(),
							is(instanceOf(NoCacheSqlFileRepository.class)));
					assertThat(config.getNaming(), is(Naming.SNAKE_LOWER_CASE));
					assertThat(config.getJdbcLogger(), is(instanceOf(UtilLoggingJdbcLogger.class)));
					assertThat(config.getEntityListenerProvider(),
							is(instanceOf(TestEntityListenerProvider.class)));
					assertThat(config.getDuplicateColumnHandler(),
							is(ConfigConfigure.testDuplicateColumnHandler));
					assertThat(config.getScriptFileLoader(),
							is(ConfigConfigure.testScriptFileLoader));
					assertThat(config.getSqlBuilderSettings(),
							is(ConfigConfigure.testSqlBuilderSettings));
					assertThat(config.getStatisticManager(),
							is(ConfigConfigure.testStatisticManager));
					PersistenceExceptionTranslator translator = context
							.getBean(PersistenceExceptionTranslator.class);
					assertThat(translator,
							is(instanceOf(DomaPersistenceExceptionTranslator.class)));
				});
	}

	@Test
	void exceptionTranslationEnabledSpecifyFalse() {
		this.contextRunner
				.withPropertyValues("doma.exception-translation-enabled=false")
				.run(context -> assertThrows(NoSuchBeanDefinitionException.class,
						() -> context.getBean(PersistenceExceptionTranslator.class)));
	}

	@Test
	void exceptionTranslationEnabledSpecifyTrue() {
		this.contextRunner
				.withPropertyValues("doma.exception-translation-enabled=true")
				.run(context -> {
					PersistenceExceptionTranslator translator = context
							.getBean(PersistenceExceptionTranslator.class);
					assertThat(translator,
							is(instanceOf(DomaPersistenceExceptionTranslator.class)));
				});
	}

	@Test
	void changeDialect() {
		this.contextRunner
				.withPropertyValues("doma.dialect=MYSQL")
				.run(context -> {
					Dialect dialect = context.getBean(Dialect.class);
					assertThat(dialect, is(instanceOf(MysqlDialect.class)));
				});
	}

	@Test
	void changeMaxRows() {
		this.contextRunner
				.withPropertyValues("doma.max-rows=100")
				.run(context -> {
					Config config = context.getBean(Config.class);
					assertThat(config.getMaxRows(), is(100));
				});
	}

	@Test
	void sqlExceptionTranslator() {
		this.contextRunner
				.run(context -> {
					PersistenceExceptionTranslator translator = context
							.getBean(PersistenceExceptionTranslator.class);
					{
						// Translated by SQLErrorCodeSQLExceptionTranslator
						DataAccessException dataAccessException = translator
								.translateExceptionIfPossible(new JdbcException(Message.DOMA2008,
										new SQLException("Acquire Lock on H2", "SqlState", 50200,
												null)));
						assertThat(dataAccessException,
								is(instanceOf(CannotAcquireLockException.class)));
					}
					{
						// Translated by SQLExceptionSubclassTranslator(fallback)
						DataAccessException dataAccessException = translator
								.translateExceptionIfPossible(new JdbcException(Message.DOMA2008,
										new SQLTimeoutException("Timeout", "SqlState", -1, null)));
						assertThat(dataAccessException,
								is(instanceOf(QueryTimeoutException.class)));
					}
					{
						// Translated by SQLStateSQLExceptionTranslator (fallback)
						DataAccessException dataAccessException = translator
								.translateExceptionIfPossible(new JdbcException(Message.DOMA2008,
										new SQLException("With check violation", "44", -1, null)));
						assertThat(dataAccessException,
								is(instanceOf(DataIntegrityViolationException.class)));
					}
				});
	}

	@Test
	void autoRegisteredCriteriaAPI() {
		this.contextRunner
				.run(context -> {
					Entityql entityql = context.getBean(Entityql.class);
					org.assertj.core.api.Assertions.assertThat(entityql).isNotNull();
					NativeSql nativeSql = context.getBean(NativeSql.class);
					org.assertj.core.api.Assertions.assertThat(nativeSql).isNotNull();
				});
	}

	@Test
	void criteriaAPIWithConfig() {
		this.contextRunner
				.withUserConfiguration(MyCriteriaAPIConfig.class)
				.run(context -> {
					Map<String, Entityql> entityqlBeans = context.getBeansOfType(Entityql.class);
					org.assertj.core.api.Assertions.assertThat(entityqlBeans.size()).isEqualTo(1);
					org.assertj.core.api.Assertions.assertThat(entityqlBeans.get("myEntityql"))
							.isNotNull();
					Map<String, NativeSql> nativeSqlBeans = context.getBeansOfType(NativeSql.class);
					org.assertj.core.api.Assertions.assertThat(nativeSqlBeans.size()).isEqualTo(1);
					org.assertj.core.api.Assertions.assertThat(nativeSqlBeans.get("myNativeSql"))
							.isNotNull();
				});
	}

	@Test
	void dialectByDataSourceUrl() {
		this.contextRunner
				.withPropertyValues(
						"spring.datasource.url=jdbc:postgresql://localhost:1234/example",
						"doma.exception-translation-enabled=false" /* prevent database connections */)
				.run(context -> {
					Dialect dialect = context.getBean(Dialect.class);
					assertThat(dialect, is(instanceOf(PostgresDialect.class)));
				});
	}

	@Test
	void dialectByJdbConnectionDetails() {
		this.contextRunner
				.withPropertyValues(
						"doma.exception-translation-enabled=false"/* prevent database connections */)
				.withBean(JdbcConnectionDetails.class, () -> new JdbcConnectionDetails() {
					@Override
					public String getUsername() {
						return "dummy";
					}

					@Override
					public String getPassword() {
						return "dummy";
					}

					@Override
					public String getJdbcUrl() {
						return "jdbc:postgresql://localhost:1234/example";
					}
				})
				.run(context -> {
					Dialect dialect = context.getBean(Dialect.class);
					assertThat(dialect, is(instanceOf(PostgresDialect.class)));
				});
	}

	@Test
	void dialectMissingJdbConnectionDetails() {
		this.contextRunner
				.withPropertyValues(
						"doma.exception-translation-enabled=false"/* prevent database connections */)

				.withBean(DataSource.class, SimpleDriverDataSource::new)
				.run(context -> assertThat(context.getStartupFailure().getMessage(), containsString(
						"No connection details available. You will probably have to set 'doma.dialect' explicitly.")));
	}

	@Test
	void dialectMissingJdbConnectionDetailsExplicitDialect() {
		this.contextRunner
				.withPropertyValues(
						"doma.dialect=POSTGRES",
						"doma.exception-translation-enabled=false"/* prevent database connections */)

				.withBean(DataSource.class, SimpleDriverDataSource::new)
				.run(context -> {
					Dialect dialect = context.getBean(Dialect.class);
					assertThat(dialect, is(instanceOf(PostgresDialect.class)));
				});
	}

	@Test
	void dialectByDomaPropertiesIgnoreDataSourceUrl() {
		this.contextRunner
				.withPropertyValues(
						"spring.datasource.url=jdbc:h2:mem:example",
						"doma.dialect=POSTGRES")

				.run(context -> {
					Dialect dialect = context.getBean(Dialect.class);
					assertThat(dialect, is(instanceOf(PostgresDialect.class)));
				});
	}

	@Test
	void jdbcLoggerSlf4J() {
		this.contextRunner
				.withPropertyValues("doma.jdbcLogger=SLF4J")

				.run(context -> {
					JdbcLogger jdbcLogger = context.getBean(JdbcLogger.class);
					assertThat(jdbcLogger.getClass().getSimpleName(), is("Slf4jJdbcLogger"));
				});
	}

	@Test
	void autoRegisteredQueryDsl() {
		this.contextRunner

				.run(context -> {
					QueryDsl queryDsl = context.getBean(QueryDsl.class);
					org.assertj.core.api.Assertions.assertThat(queryDsl).isNotNull();
				});
	}

	@Test
	void queryDslWithConfig() {
		this.contextRunner
				.withUserConfiguration(MyQueryDslConfig.class)

				.run(context -> {
					Map<String, QueryDsl> queryDslBeans = context.getBeansOfType(QueryDsl.class);
					org.assertj.core.api.Assertions.assertThat(queryDslBeans.size()).isEqualTo(1);
					org.assertj.core.api.Assertions.assertThat(queryDslBeans.get("myQueryDsl"))
							.isNotNull();
				});
	}

	@Test
	void throwExceptionIfDuplicateColumn() {
		this.contextRunner
				.withPropertyValues("doma.throw-exception-if-duplicate-column=true")

				.run(context -> {
					Config config = context.getBean(Config.class);
					assertThat(config.getDuplicateColumnHandler(),
							is(instanceOf(ThrowingDuplicateColumnHandler.class)));
				});
	}

	@Test
	void customizeShouldRemoveBlockComment() {
		Predicate<String> predicate = mock(Predicate.class);
		when(predicate.test(anyString())).thenReturn(true);

		this.contextRunner

				.withBean("shouldRemoveBlockComment", Predicate.class, () -> predicate)
				.run(context -> {
					Config config = context.getBean(Config.class);
					config.getSqlBuilderSettings()
							.shouldRemoveBlockComment("shouldRemoveBlockComment");
					config.getSqlBuilderSettings()
							.shouldRemoveLineComment("shouldRemoveLineComment");

					verify(predicate, times(1)).test("shouldRemoveBlockComment");
					verifyNoMoreInteractions(predicate);
				});
	}

	@Test
	void customizeShouldRemoveLineComment() {
		Predicate<String> predicate = mock(Predicate.class);
		when(predicate.test(anyString())).thenReturn(true);

		this.contextRunner

				.withBean("shouldRemoveLineComment", Predicate.class, () -> predicate)
				.run(context -> {
					Config config = context.getBean(Config.class);
					config.getSqlBuilderSettings()
							.shouldRemoveBlockComment("shouldRemoveBlockComment");
					config.getSqlBuilderSettings()
							.shouldRemoveLineComment("shouldRemoveLineComment");

					verify(predicate, times(1)).test("shouldRemoveLineComment");
					verifyNoMoreInteractions(predicate);
				});
	}

	@Test
	void anonymousPredicateAreNotAffected() {
		Predicate<String> predicate = mock(Predicate.class);
		when(predicate.test(anyString())).thenReturn(true);

		this.contextRunner

				.withBean(Predicate.class, () -> predicate)
				.run(context -> {
					Config config = context.getBean(Config.class);
					config.getSqlBuilderSettings()
							.shouldRemoveBlockComment("shouldRemoveBlockComment");
					config.getSqlBuilderSettings()
							.shouldRemoveLineComment("shouldRemoveLineComment");

					verifyNoInteractions(predicate);
				});
	}

	@Test
	void shouldRemoveBlankLinesDefaultValue() {
		this.contextRunner

				.run(context -> {
					Config config = context.getBean(Config.class);
					org.assertj.core.api.Assertions
							.assertThat(config.getSqlBuilderSettings().shouldRemoveBlankLines())
							.isFalse();
				});
	}

	@Test
	void shouldRemoveBlankLinesChangedValue() {
		this.contextRunner
				.withPropertyValues("doma.sql-builder-settings.should-remove-blank-lines=true")

				.run(context -> {
					Config config = context.getBean(Config.class);
					org.assertj.core.api.Assertions
							.assertThat(config.getSqlBuilderSettings().shouldRemoveBlankLines())
							.isTrue();
				});
	}

	@Test
	void shouldRequireInListPaddingDefaultValue() {
		this.contextRunner

				.run(context -> {
					Config config = context.getBean(Config.class);
					org.assertj.core.api.Assertions
							.assertThat(config.getSqlBuilderSettings().shouldRequireInListPadding())
							.isFalse();
				});
	}

	@Test
	void shouldRequireInListPaddingChangedValue() {
		this.contextRunner
				.withPropertyValues("doma.sql-builder-settings.should-require-in-list-padding=true")

				.run(context -> {
					Config config = context.getBean(Config.class);
					org.assertj.core.api.Assertions
							.assertThat(config.getSqlBuilderSettings().shouldRequireInListPadding())
							.isTrue();
				});
	}

	@Test
	void statisticManagerDefaultValue() {
		this.contextRunner

				.run(context -> {
					Config config = context.getBean(Config.class);
					org.assertj.core.api.Assertions
							.assertThat(config.getStatisticManager().isEnabled()).isFalse();
				});
	}

	@Test
	void statisticManagerChangedValue() {
		this.contextRunner
				.withPropertyValues("doma.statistic-manager.enabled=true")

				.run(context -> {
					Config config = context.getBean(Config.class);
					org.assertj.core.api.Assertions
							.assertThat(config.getStatisticManager().isEnabled()).isTrue();
				});
	}

	@Configuration
	public static class ConfigBuilderConfigure {
		static DuplicateColumnHandler testDuplicateColumnHandler = new DuplicateColumnHandler() {
		};
		static ScriptFileLoader testScriptFileLoader = new ScriptFileLoader() {
		};
		static SqlBuilderSettings testSqlBuilderSettings = new SqlBuilderSettings() {
		};
		static StatisticManager testStatisticManager = new DefaultStatisticManager();

		@Bean
		DomaConfigBuilder myDomaConfigBuilder(DomaProperties domaProperties) {
			return new DomaConfigBuilder(domaProperties).dialect(new MysqlDialect())
					.sqlFileRepository(new NoCacheSqlFileRepository())
					.jdbcLogger(new UtilLoggingJdbcLogger())
					.naming(Naming.SNAKE_UPPER_CASE)
					.entityListenerProvider(new TestEntityListenerProvider())
					.duplicateColumnHandler(testDuplicateColumnHandler)
					.scriptFileLoader(testScriptFileLoader)
					.sqlBuilderSettings(testSqlBuilderSettings)
					.statisticManager(testStatisticManager);
		}
	}

	@Configuration
	public static class ConfigConfigure {
		static DuplicateColumnHandler testDuplicateColumnHandler = new DuplicateColumnHandler() {
		};
		static ScriptFileLoader testScriptFileLoader = new ScriptFileLoader() {
		};
		static SqlBuilderSettings testSqlBuilderSettings = new SqlBuilderSettings() {
		};
		static StatisticManager testStatisticManager = new DefaultStatisticManager();

		@Bean
		Config myConfig(DataSource dataSource) {
			return new Config() {
				@Override
				public DataSource getDataSource() {
					return new TransactionAwareDataSourceProxy(dataSource);
				}

				@Override
				public Dialect getDialect() {
					return new PostgresDialect();
				}

				@Override
				public SqlFileRepository getSqlFileRepository() {
					return new NoCacheSqlFileRepository();
				}

				@Override
				public Naming getNaming() {
					return Naming.SNAKE_LOWER_CASE;
				}

				@Override
				public JdbcLogger getJdbcLogger() {
					return new UtilLoggingJdbcLogger();
				}

				@Override
				public EntityListenerProvider getEntityListenerProvider() {
					return new TestEntityListenerProvider();
				}

				@Override
				public DuplicateColumnHandler getDuplicateColumnHandler() {
					return testDuplicateColumnHandler;
				}

				@Override
				public ScriptFileLoader getScriptFileLoader() {
					return testScriptFileLoader;
				}

				@Override
				public SqlBuilderSettings getSqlBuilderSettings() {
					return testSqlBuilderSettings;
				}

				@Override
				public StatisticManager getStatisticManager() {
					return testStatisticManager;
				}
			};
		}
	}

	static class TestEntityListenerProvider implements EntityListenerProvider {
	}

	@Configuration
	public static class MyCriteriaAPIConfig {

		@Bean
		public Entityql myEntityql(Config config) {
			return new Entityql(config);
		}

		@Bean
		public NativeSql myNativeSql(Config config) {
			return new NativeSql(config);
		}
	}

	@Configuration
	public static class MyQueryDslConfig {

		@Bean
		public QueryDsl myQueryDsl(Config config) {
			return new QueryDsl(config);
		}
	}
}
