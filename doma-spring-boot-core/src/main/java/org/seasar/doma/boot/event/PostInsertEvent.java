package org.seasar.doma.boot.event;

import org.seasar.doma.jdbc.entity.PostInsertContext;

public class PostInsertEvent<T> extends DomaEvent<T, PostInsertContext<T>> {

	public PostInsertEvent(T source, PostInsertContext<T> context) {
		super(source, context);
	}
}
