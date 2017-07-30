package org.seasar.doma.boot.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;
import org.seasar.doma.Entity;
import org.seasar.doma.boot.event.annotation.HandleDomaEvent;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

public class DomaApplicationListener implements ApplicationListener<DomaEvent<?, ?>> {

    private final Set<Class<?>> contextClasses;
    private final String beanName;
    private final Method method;
    private final BeanFactory beanFactory;

    public DomaApplicationListener(String beanName, Method method, BeanFactory beanFactory) {

        int parameterCount = method.getParameterCount();
        if (parameterCount < 1) {
            throw new IllegalArgumentException("Must receive an entity");
        } else if (parameterCount > 2) {
            throw new IllegalArgumentException("Too many parameters");
        }

        Class<?> entityClass = method.getParameterTypes()[0];
        if (entityClass.isAnnotationPresent(Entity.class) == false) {
            throw new IllegalArgumentException("First parameter must be entity class");
        }

        Set<Class<?>> contextClasses = Collections.newSetFromMap(new IdentityHashMap<>());
        Annotation[] annotations = AnnotationUtils.getAnnotations(method);
        for (Annotation annotation : annotations) {
            HandleDomaEvent handleDomaEvent = AnnotationUtils
                    .findAnnotation(annotation.annotationType(), HandleDomaEvent.class);
            if (handleDomaEvent != null) {
                contextClasses.add(handleDomaEvent.contextClass());
            }
        }

        if (parameterCount == 2) {
            if (contextClasses.size() > 1) {
                throw new IllegalArgumentException(
                        "To annotate with multi annotations must be only entity parameter");
            }

            Class<?> contextClass = method.getParameterTypes()[1];
            if (contextClass != contextClasses.iterator().next()) {
                throw new IllegalArgumentException(
                        "Mismatch between annotation and event context");
            }
            Type t = method.getGenericParameterTypes()[1];
            if (t instanceof ParameterizedType) {
                Type typeArg = ((ParameterizedType) t).getActualTypeArguments()[0];
                if (typeArg != entityClass) {
                    throw new IllegalArgumentException(
                            "Mismatch between entity class and variable bound to event context");
                }
            }
        }

        ReflectionUtils.makeAccessible(method);

        this.contextClasses = Objects.requireNonNull(contextClasses);
        this.beanName = Objects.requireNonNull(beanName);
        this.method = Objects.requireNonNull(method);
        this.beanFactory = Objects.requireNonNull(beanFactory);
    }

    @Override
    public void onApplicationEvent(DomaEvent<?, ?> event) {
        Object entity = event.getSource();
        Object context = event.getContext();
        if (shouldHandle(context.getClass())
                && entity.getClass() == method.getParameterTypes()[0]) {
            Object[] args;
            if (method.getParameterCount() == 1) {
                args = new Object[] { entity };
            } else {
                args = new Object[] { entity, context };
            }
            Object target = beanFactory.getBean(beanName);
            ReflectionUtils.invokeMethod(method, target, args);
        }
    }

    private boolean shouldHandle(Class<?> clazz) {
        for (Class<?> contextClasse : contextClasses) {
            if (contextClasse.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }
}
