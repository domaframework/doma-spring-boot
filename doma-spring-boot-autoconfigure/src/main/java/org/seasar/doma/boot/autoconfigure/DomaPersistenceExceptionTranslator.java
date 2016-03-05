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

import java.sql.SQLException;

import org.seasar.doma.jdbc.*;
import org.springframework.dao.*;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

/**
 * Converts Doma's {@link JdbcException} into Spring's {@link DataAccessException}.
 * @author Toshiaki Maki
 * @author Kazuki Shimizu
 */
public class DomaPersistenceExceptionTranslator implements PersistenceExceptionTranslator {

	private final SQLExceptionTranslator translator;

	public DomaPersistenceExceptionTranslator(SQLExceptionTranslator sqlExceptionTranslator) {
		this.translator = sqlExceptionTranslator;
	}

	@Override
	public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
		if (!(ex instanceof JdbcException)) {
			// Fallback to other translators if not JdbcException
			return null;
		}

		if (ex.getCause() instanceof SQLException) {
			SQLException e = (SQLException) ex.getCause();
			String sql = null;
			if (ex instanceof SqlExecutionException) {
				sql = ((SqlExecutionException) ex).getRawSql();
			}
			else if (ex instanceof UniqueConstraintException) {
				sql = ((UniqueConstraintException) ex).getRawSql();
			}
			return translator.translate(ex.getMessage(), sql, e);
		}
		if (ex instanceof OptimisticLockException) {
			return new OptimisticLockingFailureException(ex.getMessage(), ex);
		}
		else if (ex instanceof UniqueConstraintException) {
			return new DuplicateKeyException(ex.getMessage(), ex);
		}
		else if (ex instanceof NonUniqueResultException
				|| ex instanceof NonSingleColumnException) {
			return new IncorrectResultSizeDataAccessException(ex.getMessage(), 1, ex);
		}
		else if (ex instanceof NoResultException) {
			return new EmptyResultDataAccessException(ex.getMessage(), 1, ex);
		}
		else if (ex instanceof UnknownColumnException
				|| ex instanceof ResultMappingException) {
			return new TypeMismatchDataAccessException(ex.getMessage(), ex);
		}

		return new UncategorizedDataAccessException(ex.getMessage(), ex) {
		};
	}
}
