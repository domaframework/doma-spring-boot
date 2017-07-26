package org.seasar.doma.boot.event;

import org.seasar.doma.jdbc.entity.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * {@link EntityListener} implementation that publishes {@link DomaEvent} and delegates
 * actual processing on the event lister class which is annoteted with
 * {@link org.seasar.doma.boot.event.annotation.DomaEventHandler}. <br>
 * <br>
 * This class extends {@link NullEntityListener} so that {@link org.seasar.doma.Entity} is
 * available with default parameters.
 *
 * @param <T> Entity class
 * @author Toshiaki Maki
 */
public class DomaEventEntityListener<T> extends NullEntityListener<T> implements
		ApplicationEventPublisherAware {

	private ApplicationEventPublisher eventPublisher;

	@Override
	public void preInsert(T t, PreInsertContext<T> context) {
		this.eventPublisher.publishEvent(new PreInsertEvent<>(t, context));
	}

	@Override
	public void preUpdate(T t, PreUpdateContext<T> context) {
		this.eventPublisher.publishEvent(new PreUpdateEvent<>(t, context));
	}

	@Override
	public void preDelete(T t, PreDeleteContext<T> context) {
		this.eventPublisher.publishEvent(new PreDeleteEvent<>(t, context));
	}

	@Override
	public void postInsert(T t, PostInsertContext<T> context) {
		this.eventPublisher.publishEvent(new PostInsertEvent<>(t, context));
	}

	@Override
	public void postUpdate(T t, PostUpdateContext<T> context) {
		this.eventPublisher.publishEvent(new PostUpdateEvent<>(t, context));
	}

	@Override
	public void postDelete(T t, PostDeleteContext<T> context) {
		this.eventPublisher.publishEvent(new PostDeleteEvent<>(t, context));
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}
}
