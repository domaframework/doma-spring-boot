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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.Collections;

import org.junit.Test;
import org.seasar.doma.DomaException;
import org.seasar.doma.jdbc.*;
import org.seasar.doma.message.Message;
import org.springframework.dao.*;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.support.SQLExceptionSubclassTranslator;

public class DomaPersistenceExceptionTranslatorTest {

	private final DomaPersistenceExceptionTranslator translator = new DomaPersistenceExceptionTranslator(
			new SQLExceptionSubclassTranslator());

	@Test
	public void testOccurNotJdbcException() {
		DataAccessException dataAccessException = translator
				.translateExceptionIfPossible(new DomaException(Message.DOMA2008));
		assertThat(dataAccessException, nullValue());
	}

	@Test
	public void testOccurSqlExecutionException() {
		DataAccessException dataAccessException = translator.translateExceptionIfPossible(
				new SqlExecutionException(SqlLogType.FORMATTED, SqlKind.SELECT,
						"select * from todo where todo_id = ?",
						"select * from todo where todo_id = '000000001'",
						"TodoDao/findOne.sql", new SQLException(), null));
		assertThat(dataAccessException, is(instanceOf(UncategorizedSQLException.class)));
		assertThat(UncategorizedSQLException.class.cast(dataAccessException).getSql(),
				is("select * from todo where todo_id = ?"));
	}

	@Test
	public void testOccurUniqueConstraintException() {
		DataAccessException dataAccessException = translator.translateExceptionIfPossible(
				new UniqueConstraintException(SqlLogType.FORMATTED, SqlKind.INSERT,
						"insert into todo (todo_id, title) values (?, ?)",
						"insert into todo (todo_id, title) values ('000000001', 'Title')",
						"TodoDao/insert.sql", new SQLException()));
		assertThat(dataAccessException, is(instanceOf(UncategorizedSQLException.class)));
		assertThat(UncategorizedSQLException.class.cast(dataAccessException).getSql(),
				is("insert into todo (todo_id, title) values (?, ?)"));
	}

	@Test
	public void testThrowOptimisticLockingFailureException() {
		DataAccessException dataAccessException = translator.translateExceptionIfPossible(
				new OptimisticLockException(SqlLogType.FORMATTED, SqlKind.SELECT,
						"update todo set title = ? where todo_id = ? and version = ?",
						"update todo set title = 'Modified Title' where todo_id = '000000001' and version = 1",
						"TodoDao/update.sql"));
		assertThat(dataAccessException,
				is(instanceOf(OptimisticLockingFailureException.class)));
	}

	@Test
	public void testThrowDuplicateKeyException() {
		DataAccessException dataAccessException = translator.translateExceptionIfPossible(
				new UniqueConstraintException(SqlLogType.FORMATTED, SqlKind.INSERT,
						"insert into todo (todo_id, title) values (?, ?)",
						"insert into todo (todo_id, title) values ('000000001', 'Title')",
						"TodoDao/insert.sql", null));
		assertThat(dataAccessException, is(instanceOf(DuplicateKeyException.class)));
	}

	@Test
	public void testThrowIncorrectResultSizeDataAccessException() {
		{
			DataAccessException dataAccessException = translator
					.translateExceptionIfPossible(new NonUniqueResultException(
							SqlLogType.FORMATTED, SqlKind.SELECT,
							"select * from todo where created_at = ?",
							"select * from todo where created_at = '2016-03-06'",
							"TodoDao/findBy.sql"));
			assertThat(dataAccessException,
					is(instanceOf(IncorrectResultSizeDataAccessException.class)));
		}
		{
			DataAccessException dataAccessException = translator
					.translateExceptionIfPossible(new NonSingleColumnException(
							SqlLogType.FORMATTED, SqlKind.SELECT,
							"select todo_id, title from todo where created_at = ?",
							"select todo_id, title from todo where created_at = '2016-03-06'",
							"TodoDao/findBy.sql"));
			assertThat(dataAccessException,
					is(instanceOf(IncorrectResultSizeDataAccessException.class)));
		}
	}

	@Test
	public void testThrowEmptyResultDataAccessException() {
		DataAccessException dataAccessException = translator
				.translateExceptionIfPossible(new NoResultException(SqlLogType.FORMATTED,
						SqlKind.SELECT, "select * from todo where todo_id = ?",
						"select * from todo where todo_id = '000000001'",
						"TodoDao/findOne.sql"));
		assertThat(dataAccessException,
				is(instanceOf(EmptyResultDataAccessException.class)));
	}

	@Test
	public void testThrowTypeMismatchDataAccessException() {
		{
			DataAccessException dataAccessException = translator
					.translateExceptionIfPossible(new UnknownColumnException(
							SqlLogType.FORMATTED, "todo_id", "todoId", "model.Todo",
							SqlKind.SELECT, "select * from todo where created_at = ?",
							"select * from todo where created_at = '2016-03-06'",
							"TodoDao/findBy.sql"));
			assertThat(dataAccessException,
					is(instanceOf(TypeMismatchDataAccessException.class)));
		}
		{
			DataAccessException dataAccessException = translator
					.translateExceptionIfPossible(new ResultMappingException(
							SqlLogType.FORMATTED, "Todo",
							Collections.singletonList("finished"),
							Collections.singletonList("modified_at"), SqlKind.SELECT,
							"select todo_id, title from todo where created_at = ?",
							"select todo_id, title from todo where created_at = '2016-03-06'",
							"TodoDao/findBy.sql"));
			assertThat(dataAccessException,
					is(instanceOf(TypeMismatchDataAccessException.class)));
		}
	}

	@Test
	public void testThrowUncategorizedDataAccessException() {
		DataAccessException dataAccessException = translator.translateExceptionIfPossible(
				new ConfigException("DomaConfig", "configure"));
		assertThat(dataAccessException,
				is(instanceOf(UncategorizedDataAccessException.class)));
	}

}
