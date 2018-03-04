package org.seasar.doma.boot.event;

import org.springframework.context.ApplicationEvent;

/**
 * Abstract base class for events emitted by Doma.
 */
public abstract class DomaEvent<T, S> extends ApplicationEvent {
	private final S context;

	protected DomaEvent(T source, S context) {
		super(source);
		this.context = context;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getSource() {
		return (T) this.source;
	}

	public S getContext() {
		return this.context;
	}
}