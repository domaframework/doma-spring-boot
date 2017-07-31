package org.seasar.doma.boot.event;

import java.lang.reflect.Method;
import org.seasar.doma.boot.event.annotation.HandleDomaEvent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListenerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;

public class DomaEventListenerFactory implements EventListenerFactory, Ordered,
		BeanFactoryAware {

	private int order = Ordered.HIGHEST_PRECEDENCE;
	private BeanFactory beanFactory;

	@Override
	public boolean supportsMethod(Method method) {
		return AnnotationUtils.findAnnotation(method, HandleDomaEvent.class) != null;
	}

	@Override
	public ApplicationListener<?> createApplicationListener(String beanName,
			Class<?> type, Method method) {
		return new DomaApplicationListener(beanName, method, beanFactory);
	}

	@Override
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
}
