package org.seasar.doma.boot;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.seasar.doma.DomaException;
import org.seasar.doma.jdbc.ConfigException;
import org.seasar.doma.jdbc.NoResultException;
import org.seasar.doma.jdbc.NonSingleColumnException;
import org.seasar.doma.jdbc.NonUniqueResultException;
import org.seasar.doma.jdbc.OptimisticLockException;
import org.seasar.doma.jdbc.ResultMappingException;
import org.seasar.doma.jdbc.SqlExecutionException;
import org.seasar.doma.jdbc.SqlKind;
import org.seasar.doma.jdbc.SqlLogType;
import org.seasar.doma.jdbc.UniqueConstraintException;
import org.seasar.doma.jdbc.UnknownColumnException;
import org.seasar.doma.message.Message;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.TypeMismatchDataAccessException;
import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.support.SQLExceptionSubclassTranslator;

class DomaPersistenceExceptionTranslatorTest {

	private final DomaPersistenceExceptionTranslator translator = new DomaPersistenceExceptionTranslator(
			new SQLExceptionSubclassTranslator());

	@Test
	void occurNotJdbcException() {
		DataAccessException dataAccessException = translator
				.translateExceptionIfPossible(new DomaException(Message.DOMA2008));
		assertThat(dataAccessException).isNull();
	}

	@Test
	void occurSqlExecutionException() {
		DataAccessException dataAccessException = translator
				.translateExceptionIfPossible(new SqlExecutionException(
						SqlLogType.FORMATTED, SqlKind.SELECT,
						"select * from todo where todo_id = ?",
						"select * from todo where todo_id = '000000001'",
						"TodoDao/findOne.sql", new SQLException(), null));
		assertThat(dataAccessException).isInstanceOf(UncategorizedSQLException.class);
		assertThat(((UncategorizedSQLException) dataAccessException).getSql())
				.isEqualTo("select * from todo where todo_id = ?");
	}

	@Test
	void throwOptimisticLockingFailureException() {
		DataAccessException dataAccessException = translator
				.translateExceptionIfPossible(new OptimisticLockException(
						SqlLogType.FORMATTED,
						SqlKind.SELECT,
						"update todo set title = ? where todo_id = ? and version = ?",
						"update todo set title = 'Modified Title' where todo_id = '000000001' and version = 1",
						"TodoDao/update.sql"));
		assertThat(dataAccessException).isInstanceOf(OptimisticLockingFailureException.class);
	}

	@Test
	void throwDuplicateKeyException() {
		DataAccessException dataAccessException = translator
				.translateExceptionIfPossible(new UniqueConstraintException(
						SqlLogType.FORMATTED,
						SqlKind.INSERT,
						"insert into todo (todo_id, title) values (?, ?)",
						"insert into todo (todo_id, title) values ('000000001', 'Title')",
						"TodoDao/insert.sql", new SQLException()));
		assertThat(dataAccessException).isInstanceOf(DuplicateKeyException.class);
	}

	@Test
	void throwIncorrectResultSizeDataAccessException() {
		{
			DataAccessException dataAccessException = translator
					.translateExceptionIfPossible(new NonUniqueResultException(
							SqlLogType.FORMATTED, SqlKind.SELECT,
							"select * from todo where created_at = ?",
							"select * from todo where created_at = '2016-03-06'",
							"TodoDao/findBy.sql"));
			assertThat(dataAccessException)
					.isInstanceOf(IncorrectResultSizeDataAccessException.class);
		}
		{
			DataAccessException dataAccessException = translator
					.translateExceptionIfPossible(new NonSingleColumnException(
							SqlLogType.FORMATTED,
							SqlKind.SELECT,
							"select todo_id, title from todo where created_at = ?",
							"select todo_id, title from todo where created_at = '2016-03-06'",
							"TodoDao/findBy.sql"));
			assertThat(dataAccessException)
					.isInstanceOf(IncorrectResultSizeDataAccessException.class);
		}
	}

	@Test
	void throwEmptyResultDataAccessException() {
		DataAccessException dataAccessException = translator
				.translateExceptionIfPossible(new NoResultException(SqlLogType.FORMATTED,
						SqlKind.SELECT, "select * from todo where todo_id = ?",
						"select * from todo where todo_id = '000000001'",
						"TodoDao/findOne.sql"));
		assertThat(dataAccessException).isInstanceOf(EmptyResultDataAccessException.class);
	}

	@Test
	void throwTypeMismatchDataAccessException() {
		{
			DataAccessException dataAccessException = translator
					.translateExceptionIfPossible(new UnknownColumnException(
							SqlLogType.FORMATTED, "todo_id", "todoId", "model.Todo",
							SqlKind.SELECT, "select * from todo where created_at = ?",
							"select * from todo where created_at = '2016-03-06'",
							"TodoDao/findBy.sql"));
			assertThat(dataAccessException).isInstanceOf(TypeMismatchDataAccessException.class);
		}
		{
			DataAccessException dataAccessException = translator
					.translateExceptionIfPossible(new ResultMappingException(
							SqlLogType.FORMATTED,
							"Todo",
							Collections.singletonList("finished"),
							Collections.singletonList("modified_at"),
							SqlKind.SELECT,
							"select todo_id, title from todo where created_at = ?",
							"select todo_id, title from todo where created_at = '2016-03-06'",
							"TodoDao/findBy.sql"));
			assertThat(dataAccessException).isInstanceOf(TypeMismatchDataAccessException.class);
		}
	}

	@Test
	void throwUncategorizedDataAccessException() {
		DataAccessException dataAccessException = translator
				.translateExceptionIfPossible(new ConfigException("DomaConfig",
						"configure"));
		assertThat(dataAccessException).isInstanceOf(UncategorizedDataAccessException.class);
	}

}
