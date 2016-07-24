package org.seasar.doma.boot.event;

import org.seasar.doma.boot.event.annotation.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AnnotatedDomaEventHandlerInvoker implements ApplicationListener<DomaEvent>,
		BeanPostProcessor {

	private final MultiValueMap<Class<? extends DomaEvent>, EventHandlerMethod> handlerMethods = new LinkedMultiValueMap<>();

	@Override
	public void onApplicationEvent(DomaEvent event) {
		Class<? extends DomaEvent> eventType = event.getClass();
		if (!handlerMethods.containsKey(eventType)) {
			return;
		}
		for (EventHandlerMethod handlerMethod : handlerMethods.get(eventType)) {
			Object src = event.getSource();
			if (!ClassUtils.isAssignable(handlerMethod.targetType, src.getClass())) {
				continue;
			}
			Object context = event.getContext();
			List<Object> parameters = new ArrayList<>();
			for (Class<?> paramType : handlerMethod.method.getParameterTypes()) {
				if (ClassUtils.isAssignable(paramType, src.getClass())) {
					parameters.add(src);
				}
				else if (ClassUtils.isAssignable(paramType, context.getClass())) {
					parameters.add(context);
				}
				else {
					parameters.add(null);
				}
			}
			ReflectionUtils.invokeMethod(handlerMethod.method, handlerMethod.handler,
					parameters.toArray());
		}
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		Class<?> beanType = ClassUtils.getUserClass(bean);
		DomaEventHandler typeAnno = AnnotationUtils.findAnnotation(beanType,
				DomaEventHandler.class);
		if (typeAnno == null) {
			return bean;
		}
		ReflectionUtils.doWithMethods(beanType, method -> {
			inspect(bean, method, HandlePreInsert.class, PreInsertEvent.class);
			inspect(bean, method, HandlePreUpdate.class, PreUpdateEvent.class);
			inspect(bean, method, HandlePreDelete.class, PreDeleteEvent.class);
			inspect(bean, method, HandlePostInsert.class, PostInsertEvent.class);
			inspect(bean, method, HandlePostUpdate.class, PostUpdateEvent.class);
			inspect(bean, method, HandlePostDelete.class, PostDeleteEvent.class);
		});
		return bean;
	}

	private <T extends Annotation> void inspect(Object handler, Method method,
			Class<T> annotationType, Class<? extends DomaEvent> eventType) {
		T annotation = AnnotationUtils.findAnnotation(method, annotationType);
		if (annotation == null) {
			return;
		}
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length == 0) {
			throw new IllegalStateException(
					String.format(
							"Invalid event handler method %s! At least a single argument is required to determine the domain type for which you are interested in events.",
							method));
		}
		EventHandlerMethod handlerMethod = new EventHandlerMethod(parameterTypes[0],
				handler, method);
		handlerMethods.add(eventType, handlerMethod);
	}

	static class EventHandlerMethod {

		final Class<?> targetType;
		final Method method;
		final Object handler;

		private EventHandlerMethod(Class<?> targetType, Object handler, Method method) {
			this.targetType = targetType;
			this.method = method;
			this.handler = handler;
			ReflectionUtils.makeAccessible(this.method);
		}

		@Override
		public String toString() {
			return String.format(
					"EventHandlerMethod{ targetType=%s, method=%s, handler=%s }",
					targetType, method, handler);
		}
	}

}
