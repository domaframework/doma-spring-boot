package org.seasar.doma.boot;

import java.util.Map;
import java.util.function.Supplier;

import org.seasar.doma.jdbc.EntityListenerProvider;
import org.seasar.doma.jdbc.entity.EntityListener;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * {@link EntityListenerProvider} implementation that {@link EntityListener} managed by
 * Spring Framework, or else created by Doma.
 *
 * @author backpaper0
 *
 */
public class TryLookupEntityListenerProvider implements EntityListenerProvider,
		ApplicationContextAware {

	private ApplicationContext context;

	@Override
	public <ENTITY, LISTENER extends EntityListener<ENTITY>> LISTENER get(
			Class<LISTENER> listenerClass, Supplier<LISTENER> listenerSupplier) {
		Map<String, LISTENER> beans = context.getBeansOfType(listenerClass);
		if (beans.size() > 1) {
			throw new IllegalStateException("Bean type of " + listenerClass
					+ " bean must be unique!");
		}
		return beans.values().stream().findAny().orElseGet(listenerSupplier);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}
}
