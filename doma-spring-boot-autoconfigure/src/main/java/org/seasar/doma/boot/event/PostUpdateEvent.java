package org.seasar.doma.boot.event;

import org.seasar.doma.jdbc.entity.PostUpdateContext;

public class PostUpdateEvent<T> extends DomaEvent<T, PostUpdateContext<T>> {

	public PostUpdateEvent(T source, PostUpdateContext<T> context) {
		super(source, context);
	}
}
