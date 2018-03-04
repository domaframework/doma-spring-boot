package org.seasar.doma.boot.event;

import org.seasar.doma.jdbc.entity.PreDeleteContext;

public class PreDeleteEvent<T> extends DomaEvent<T, PreDeleteContext<T>> {

	public PreDeleteEvent(T source, PreDeleteContext<T> context) {
		super(source, context);
	}
}
