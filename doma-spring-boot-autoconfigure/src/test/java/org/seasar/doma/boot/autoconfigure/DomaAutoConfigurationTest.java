package org.seasar.doma.boot.autoconfigure;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
	void testAutoRegisteredConfig() {
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
	void testConfigWithDomaConfigBuilder() {
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
	void testConfigWithConfig() {
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
	void testExceptionTranslationEnabledSpecifyFalse() {
		this.contextRunner
				.withPropertyValues("doma.exception-translation-enabled=false")
				.run(context -> {
					assertThrows(NoSuchBeanDefinitionException.class,
							() -> context.getBean(PersistenceExceptionTranslator.class));
				});
	}

	@Test
	void testExceptionTranslationEnabledSpecifyTrue() {
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
	void testChangeDialect() {
		this.contextRunner
				.withPropertyValues("doma.dialect=MYSQL")
				.run(context -> {
					Dialect dialect = context.getBean(Dialect.class);
					assertThat(dialect, is(instanceOf(MysqlDialect.class)));
				});
	}

	@Test
	void testChangeMaxRows() {
		this.contextRunner
				.withPropertyValues("doma.max-rows=100")
				.run(context -> {
					Config config = context.getBean(Config.class);
					assertThat(config.getMaxRows(), is(100));
				});
	}

	@Test
	void testSQLExceptionTranslator() {
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
	void testAutoRegisteredCriteriaAPI() {
		this.contextRunner
				.run(context -> {
					Entityql entityql = context.getBean(Entityql.class);
					assertNotNull(entityql);
					NativeSql nativeSql = context.getBean(NativeSql.class);
					assertNotNull(nativeSql);
				});
	}

	@Test
	void testCriteriaAPIWithConfig() {
		this.contextRunner
				.withUserConfiguration(MyCriteriaAPIConfig.class)
				.run(context -> {
					Map<String, Entityql> entityqlBeans = context.getBeansOfType(Entityql.class);
					assertEquals(1, entityqlBeans.size());
					assertNotNull(entityqlBeans.get("myEntityql"));
					Map<String, NativeSql> nativeSqlBeans = context.getBeansOfType(NativeSql.class);
					assertEquals(1, nativeSqlBeans.size());
					assertNotNull(nativeSqlBeans.get("myNativeSql"));
				});
	}

	@Test
	void testDialectByDataSourceUrl() {
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
	void testDialectByJdbConnectionDetails() {
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
	void testDialectMissingJdbConnectionDetails() {
		this.contextRunner
				.withPropertyValues(
						"doma.exception-translation-enabled=false"/* prevent database connections */)

				.withBean(DataSource.class, SimpleDriverDataSource::new)
				.run(context -> {
					assertThat(context.getStartupFailure().getMessage(), containsString(
							"No connection details available. You will probably have to set 'doma.dialect' explicitly."));
				});
	}

	@Test
	void testDialectMissingJdbConnectionDetailsExplicitDialect() {
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
	void testDialectByDomaPropertiesIgnoreDataSourceUrl() {
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
	void testJdbcLoggerSlf4J() {
		this.contextRunner
				.withPropertyValues("doma.jdbcLogger=SLF4J")

				.run(context -> {
					JdbcLogger jdbcLogger = context.getBean(JdbcLogger.class);
					assertThat(jdbcLogger.getClass().getSimpleName(), is("Slf4jJdbcLogger"));
				});
	}

	@Test
	void testAutoRegisteredQueryDsl() {
		this.contextRunner

				.run(context -> {
					QueryDsl queryDsl = context.getBean(QueryDsl.class);
					assertNotNull(queryDsl);
				});
	}

	@Test
	void testQueryDslWithConfig() {
		this.contextRunner
				.withUserConfiguration(MyQueryDslConfig.class)

				.run(context -> {
					Map<String, QueryDsl> queryDslBeans = context.getBeansOfType(QueryDsl.class);
					assertEquals(1, queryDslBeans.size());
					assertNotNull(queryDslBeans.get("myQueryDsl"));
				});
	}

	@Test
	void testThrowExceptionIfDuplicateColumn() {
		this.contextRunner
				.withPropertyValues("doma.throw-exception-if-duplicate-column=true")

				.run(context -> {
					Config config = context.getBean(Config.class);
					assertThat(config.getDuplicateColumnHandler(),
							is(instanceOf(ThrowingDuplicateColumnHandler.class)));
				});
	}

	@Test
	void testCustomizeShouldRemoveBlockComment() {
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
	void testCustomizeShouldRemoveLineComment() {
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
	void testAnonymousPredicateAreNotAffected() {
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
	void testShouldRemoveBlankLinesDefaultValue() {
		this.contextRunner

				.run(context -> {
					Config config = context.getBean(Config.class);
					assertFalse(config.getSqlBuilderSettings().shouldRemoveBlankLines());
				});
	}

	@Test
	void testShouldRemoveBlankLinesChangedValue() {
		this.contextRunner
				.withPropertyValues("doma.sql-builder-settings.should-remove-blank-lines=true")

				.run(context -> {
					Config config = context.getBean(Config.class);
					assertTrue(config.getSqlBuilderSettings().shouldRemoveBlankLines());
				});
	}

	@Test
	void testShouldRequireInListPaddingDefaultValue() {
		this.contextRunner

				.run(context -> {
					Config config = context.getBean(Config.class);
					assertFalse(config.getSqlBuilderSettings().shouldRequireInListPadding());
				});
	}

	@Test
	void testShouldRequireInListPaddingChangedValue() {
		this.contextRunner
				.withPropertyValues("doma.sql-builder-settings.should-require-in-list-padding=true")

				.run(context -> {
					Config config = context.getBean(Config.class);
					assertTrue(config.getSqlBuilderSettings().shouldRequireInListPadding());
				});
	}

	@Test
	void testStatisticManagerDefaultValue() {
		this.contextRunner

				.run(context -> {
					Config config = context.getBean(Config.class);
					assertFalse(config.getStatisticManager().isEnabled());
				});
	}

	@Test
	void testStatisticManagerChangedValue() {
		this.contextRunner
				.withPropertyValues("doma.statistic-manager.enabled=true")

				.run(context -> {
					Config config = context.getBean(Config.class);
					assertTrue(config.getStatisticManager().isEnabled());
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
