package org.seasar.doma.boot.event;

import org.seasar.doma.jdbc.entity.PostDeleteContext;

public class PostDeleteEvent<T> extends DomaEvent<T, PostDeleteContext<T>> {

	public PostDeleteEvent(T source, PostDeleteContext<T> context) {
		super(source, context);
	}
}
