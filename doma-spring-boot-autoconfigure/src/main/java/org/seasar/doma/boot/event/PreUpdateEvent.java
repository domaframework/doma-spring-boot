package org.seasar.doma.boot.event;

import org.seasar.doma.jdbc.entity.PreUpdateContext;

public class PreUpdateEvent<T> extends DomaEvent<T, PreUpdateContext<T>> {

	public PreUpdateEvent(T source, PreUpdateContext<T> context) {
		super(source, context);
	}
}
