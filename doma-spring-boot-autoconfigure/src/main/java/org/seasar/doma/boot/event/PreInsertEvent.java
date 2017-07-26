package org.seasar.doma.boot.event;

import org.seasar.doma.jdbc.entity.PreInsertContext;

public class PreInsertEvent<T> extends DomaEvent<T, PreInsertContext<T>> {

	public PreInsertEvent(T source, PreInsertContext<T> context) {
		super(source, context);
	}
}
