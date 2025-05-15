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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

public class DomaAutoConfigurationTest {
	AnnotationConfigApplicationContext context;

	@Before
	public void setUp() {
		this.context = new AnnotationConfigApplicationContext();
	}

	@Test
	public void testAutoRegisteredConfig() {
		this.context.register(DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		Config config = this.context.getBean(Config.class);
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
		assertThat(config.getStatisticManager(), is(instanceOf(DefaultStatisticManager.class)));
		PersistenceExceptionTranslator translator = this.context
				.getBean(PersistenceExceptionTranslator.class);
		assertThat(translator, is(instanceOf(DomaPersistenceExceptionTranslator.class)));
	}

	@Test
	public void testConfigWithDomaConfigBuilder() {
		this.context.register(ConfigBuilderConfigure.class, DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		Config config = this.context.getBean(Config.class);
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
		assertThat(config.getScriptFileLoader(), is(ConfigBuilderConfigure.testScriptFileLoader));
		assertThat(config.getSqlBuilderSettings(),
				is(ConfigBuilderConfigure.testSqlBuilderSettings));
		assertThat(config.getStatisticManager(), is(ConfigBuilderConfigure.testStatisticManager));
		PersistenceExceptionTranslator translator = this.context
				.getBean(PersistenceExceptionTranslator.class);
		assertThat(translator, is(instanceOf(DomaPersistenceExceptionTranslator.class)));
	}

	@Test
	public void testConfigWithConfig() {
		this.context.register(ConfigConfigure.class, DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		Config config = this.context.getBean(Config.class);
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
		assertThat(config.getScriptFileLoader(), is(ConfigConfigure.testScriptFileLoader));
		assertThat(config.getSqlBuilderSettings(),
				is(ConfigConfigure.testSqlBuilderSettings));
		assertThat(config.getStatisticManager(), is(ConfigConfigure.testStatisticManager));
		PersistenceExceptionTranslator translator = this.context
				.getBean(PersistenceExceptionTranslator.class);
		assertThat(translator, is(instanceOf(DomaPersistenceExceptionTranslator.class)));
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void testExceptionTranslationEnabledSpecifyFalse() {
		EnvironmentTestUtils.addEnvironment(this.context,
				"doma.exception-translation-enabled:false");
		this.context.register(DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		this.context.getBean(PersistenceExceptionTranslator.class);
	}

	@Test
	public void testExceptionTranslationEnabledSpecifyTrue() {
		EnvironmentTestUtils.addEnvironment(this.context,
				"doma.exception-translation-enabled:true");
		this.context.register(DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		PersistenceExceptionTranslator translator = this.context
				.getBean(PersistenceExceptionTranslator.class);
		assertThat(translator, is(instanceOf(DomaPersistenceExceptionTranslator.class)));
	}

	@Test
	public void testChangeDialect() {
		EnvironmentTestUtils.addEnvironment(this.context, "doma.dialect:MYSQL");
		this.context.register(DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		Dialect dialect = this.context.getBean(Dialect.class);
		assertThat(dialect, is(instanceOf(MysqlDialect.class)));
	}

	@Test
	public void testChangeMaxRows() {
		EnvironmentTestUtils.addEnvironment(this.context, "doma.max-rows:100");
		this.context.register(DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		Config config = this.context.getBean(Config.class);
		assertThat(config.getMaxRows(), is(100));
	}

	@Test
	public void testSQLExceptionTranslator() {
		this.context.register(DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		PersistenceExceptionTranslator translator = this.context
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
			assertThat(dataAccessException, is(instanceOf(QueryTimeoutException.class)));
		}
		{
			// Translated by SQLStateSQLExceptionTranslator (fallback)
			DataAccessException dataAccessException = translator
					.translateExceptionIfPossible(new JdbcException(Message.DOMA2008,
							new SQLException("With check violation", "44", -1, null)));
			assertThat(dataAccessException,
					is(instanceOf(DataIntegrityViolationException.class)));
		}
	}

	@Test
	public void testAutoRegisteredCriteriaAPI() {
		this.context.register(DomaAutoConfiguration.class, DataSourceAutoConfiguration.class);
		this.context.refresh();
		Entityql entityql = this.context.getBean(Entityql.class);
		assertNotNull(entityql);
		NativeSql nativeSql = this.context.getBean(NativeSql.class);
		assertNotNull(nativeSql);
	}

	@Test
	public void testCriteriaAPIWithConfig() {
		this.context.register(MyCriteriaAPIConfig.class, DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		Map<String, Entityql> entityqlBeans = this.context.getBeansOfType(Entityql.class);
		assertEquals(1, entityqlBeans.size());
		assertNotNull(entityqlBeans.get("myEntityql"));
		Map<String, NativeSql> nativeSqlBeans = this.context.getBeansOfType(NativeSql.class);
		assertEquals(1, nativeSqlBeans.size());
		assertNotNull(nativeSqlBeans.get("myNativeSql"));
	}

	@Test
	public void testDialectByDataSourceUrl() {
		MutablePropertySources sources = context.getEnvironment()
				.getPropertySources();
		sources.addFirst(new MapPropertySource("test",
				Map.of("spring.datasource.url", "jdbc:postgresql://localhost:1234/example",
						"doma.exception-translation-enabled",
						"false" /* prevent database connections */)));
		this.context.register(DomaAutoConfiguration.class, DataSourceAutoConfiguration.class);
		this.context.refresh();
		Dialect dialect = this.context.getBean(Dialect.class);
		assertThat(dialect, is(instanceOf(PostgresDialect.class)));
	}

	@Test
	public void testDialectByJdbConnectionDetails() {
		MutablePropertySources sources = context.getEnvironment()
				.getPropertySources();
		sources.addFirst(new MapPropertySource("test",
				Map.of("doma.exception-translation-enabled",
						"false"/* prevent database connections */)));
		this.context.register(DomaAutoConfiguration.class, DataSourceAutoConfiguration.class);
		this.context.registerBean(JdbcConnectionDetails.class, () -> new JdbcConnectionDetails() {
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
		});
		this.context.refresh();
		Dialect dialect = this.context.getBean(Dialect.class);
		assertThat(dialect, is(instanceOf(PostgresDialect.class)));
	}

	@Test
	public void testDialectMissingJdbConnectionDetails() {
		MutablePropertySources sources = context.getEnvironment()
				.getPropertySources();
		sources.addFirst(new MapPropertySource("test",
				Map.of("doma.exception-translation-enabled",
						"false"/* prevent database connections */)));
		this.context.register(DomaAutoConfiguration.class, DataSourceAutoConfiguration.class);
		this.context.registerBean(DataSource.class, SimpleDriverDataSource::new);
		BeanCreationException exception = assertThrows(BeanCreationException.class,
				() -> this.context.refresh());
		assertThat(exception.getMessage(), containsString(
				"No JdbcConnectionDetails available. You will need to explicitly set 'doma.dialect' property in your configuration."));
	}

	@Test
	public void testDialectMissingJdbConnectionDetailsExplicitDialect() {
		MutablePropertySources sources = context.getEnvironment()
				.getPropertySources();
		sources.addFirst(new MapPropertySource("test",
				Map.of("doma.dialect", "POSTGRES", "doma.exception-translation-enabled",
						"false"/* prevent database connections */)));
		this.context.register(DomaAutoConfiguration.class, DataSourceAutoConfiguration.class);
		this.context.registerBean(DataSource.class, SimpleDriverDataSource::new);
		this.context.refresh();
		Dialect dialect = this.context.getBean(Dialect.class);
		assertThat(dialect, is(instanceOf(PostgresDialect.class)));
	}

	@Test
	public void testDialectByDomaPropertiesIgnoreDataSourceUrl() {
		MutablePropertySources sources = context.getEnvironment()
				.getPropertySources();
		Map<String, Object> source = new HashMap<>();
		source.put("spring.datasource.url", "jdbc:h2:mem:example");
		source.put("doma.dialect", "POSTGRES");
		sources.addFirst(new MapPropertySource("test", source));
		this.context.register(DomaAutoConfiguration.class, DataSourceAutoConfiguration.class);
		this.context.refresh();
		Dialect dialect = this.context.getBean(Dialect.class);
		assertThat(dialect, is(instanceOf(PostgresDialect.class)));
	}

	@Test
	public void testJdbcLoggerSlf4J() {
		MutablePropertySources sources = context.getEnvironment()
				.getPropertySources();
		Map<String, Object> source = new HashMap<>();
		source.put("doma.jdbcLogger", "SLF4J");
		sources.addFirst(new MapPropertySource("test", source));
		this.context.register(DomaAutoConfiguration.class, DataSourceAutoConfiguration.class);
		this.context.refresh();
		JdbcLogger jdbcLogger = this.context.getBean(JdbcLogger.class);
		assertThat(jdbcLogger.getClass().getSimpleName(), is("Slf4jJdbcLogger"));
	}

	@Test
	public void testAutoRegisteredQueryDsl() {
		this.context.register(DomaAutoConfiguration.class, DataSourceAutoConfiguration.class);
		this.context.refresh();
		QueryDsl queryDsl = this.context.getBean(QueryDsl.class);
		assertNotNull(queryDsl);
	}

	@Test
	public void testQueryDslWithConfig() {
		this.context.register(MyQueryDslConfig.class, DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		Map<String, QueryDsl> queryDslBeans = this.context.getBeansOfType(QueryDsl.class);
		assertEquals(1, queryDslBeans.size());
		assertNotNull(queryDslBeans.get("myQueryDsl"));
	}

	@Test
	public void testThrowExceptionIfDuplicateColumn() {
		EnvironmentTestUtils.addEnvironment(this.context,
				"doma.throw-exception-if-duplicate-column:true");
		this.context.register(DomaAutoConfiguration.class, DataSourceAutoConfiguration.class);
		this.context.refresh();
		Config config = this.context.getBean(Config.class);
		assertThat(config.getDuplicateColumnHandler(),
				is(instanceOf(ThrowingDuplicateColumnHandler.class)));
	}

	@Test
	public void testCustomizeShouldRemoveBlockComment() {
		Predicate<String> predicate = mock(Predicate.class);
		when(predicate.test(anyString())).thenReturn(true);

		this.context.register(DomaAutoConfiguration.class, DataSourceAutoConfiguration.class);
		this.context.registerBean("shouldRemoveBlockComment", Predicate.class, () -> predicate);
		this.context.refresh();
		Config config = this.context.getBean(Config.class);
		config.getSqlBuilderSettings().shouldRemoveBlockComment("shouldRemoveBlockComment");
		config.getSqlBuilderSettings().shouldRemoveLineComment("shouldRemoveLineComment");

		verify(predicate, times(1)).test("shouldRemoveBlockComment");
		verifyNoMoreInteractions(predicate);
	}

	@Test
	public void testCustomizeShouldRemoveLineComment() {
		Predicate<String> predicate = mock(Predicate.class);
		when(predicate.test(anyString())).thenReturn(true);

		this.context.register(DomaAutoConfiguration.class, DataSourceAutoConfiguration.class);
		this.context.registerBean("shouldRemoveLineComment", Predicate.class, () -> predicate);
		this.context.refresh();
		Config config = this.context.getBean(Config.class);
		config.getSqlBuilderSettings().shouldRemoveBlockComment("shouldRemoveBlockComment");
		config.getSqlBuilderSettings().shouldRemoveLineComment("shouldRemoveLineComment");

		verify(predicate, times(1)).test("shouldRemoveLineComment");
		verifyNoMoreInteractions(predicate);
	}

	@Test
	public void testAnonymousPredicateAreNotAffected() {
		Predicate<String> predicate = mock(Predicate.class);
		when(predicate.test(anyString())).thenReturn(true);

		this.context.register(DomaAutoConfiguration.class, DataSourceAutoConfiguration.class);
		this.context.registerBean(Predicate.class, () -> predicate);
		this.context.refresh();
		Config config = this.context.getBean(Config.class);
		config.getSqlBuilderSettings().shouldRemoveBlockComment("shouldRemoveBlockComment");
		config.getSqlBuilderSettings().shouldRemoveLineComment("shouldRemoveLineComment");

		verifyNoInteractions(predicate);
	}

	@Test
	public void testShouldRemoveBlankLinesDefaultValue() {
		this.context.register(DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		Config config = this.context.getBean(Config.class);
		assertFalse(config.getSqlBuilderSettings().shouldRemoveBlankLines());
	}

	@Test
	public void testShouldRemoveBlankLinesChangedValue() {
		EnvironmentTestUtils.addEnvironment(this.context,
				"doma.sql-builder-settings.should-remove-blank-lines:true");
		this.context.register(DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		Config config = this.context.getBean(Config.class);
		assertTrue(config.getSqlBuilderSettings().shouldRemoveBlankLines());
	}

	@Test
	public void testShouldRequireInListPaddingDefaultValue() {
		this.context.register(DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		Config config = this.context.getBean(Config.class);
		assertFalse(config.getSqlBuilderSettings().shouldRequireInListPadding());
	}

	@Test
	public void testShouldRequireInListPaddingChangedValue() {
		EnvironmentTestUtils.addEnvironment(this.context,
				"doma.sql-builder-settings.should-require-in-list-padding:true");
		this.context.register(DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		Config config = this.context.getBean(Config.class);
		assertTrue(config.getSqlBuilderSettings().shouldRequireInListPadding());
	}

	@Test
	public void testStatisticManagerDefaultValue() {
		this.context.register(DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		Config config = this.context.getBean(Config.class);
		assertFalse(config.getStatisticManager().isEnabled());
	}

	@Test
	public void testStatisticManagerChangedValue() {
		EnvironmentTestUtils.addEnvironment(this.context, "doma.statistic-manager.enabled:true");
		this.context.register(DomaAutoConfiguration.class,
				DataSourceAutoConfiguration.class);
		this.context.refresh();
		Config config = this.context.getBean(Config.class);
		assertTrue(config.getStatisticManager().isEnabled());
	}

	@After
	public void tearDown() {
		if (this.context != null) {
			this.context.close();
		}
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

	private static class EnvironmentTestUtils {
		public static void addEnvironment(ConfigurableApplicationContext context,
				String pair) {
			MutablePropertySources sources = context.getEnvironment()
					.getPropertySources();
			String[] split = pair.split(":");
			String key = split[0];
			String value = split[1];
			sources.addFirst(new MapPropertySource("test", Collections.singletonMap(key,
					value)));
		}
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
