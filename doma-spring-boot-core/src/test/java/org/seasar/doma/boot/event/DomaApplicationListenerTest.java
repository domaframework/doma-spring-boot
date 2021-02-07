package org.seasar.doma.boot.event;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.seasar.doma.Entity;
import org.seasar.doma.boot.event.annotation.HandlePostInsert;
import org.seasar.doma.boot.event.annotation.HandlePreInsert;
import org.seasar.doma.jdbc.entity.PostInsertContext;
import org.seasar.doma.jdbc.entity.PreInsertContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.EventListenerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

public class DomaApplicationListenerTest {

	@Nested
	public class ConstructorTest {

		@Test
		public void entityOnly() throws Exception {
			String beanName = "";
			Method method = ConstructorTestSource.EntityOnly.class.getDeclaredMethod("handle",
					ConstructorTestSource.TestEntity1.class);
			BeanFactory beanFactory = mock(BeanFactory.class);
			new DomaApplicationListener(beanName, method, beanFactory);
		}

		@Test
		public void entityWithContext() throws Exception {
			String beanName = "";
			Method method = ConstructorTestSource.EntityWithContext.class.getDeclaredMethod("handle",
					ConstructorTestSource.TestEntity1.class, PreInsertContext.class);
			BeanFactory beanFactory = mock(BeanFactory.class);
			new DomaApplicationListener(beanName, method, beanFactory);
		}

		@Test
		public void multiAnnotations() throws Exception {
			String beanName = "";
			Method method = ConstructorTestSource.MultiAnnotations.class.getDeclaredMethod("handle",
					ConstructorTestSource.TestEntity1.class);
			BeanFactory beanFactory = mock(BeanFactory.class);
			new DomaApplicationListener(beanName, method, beanFactory);
		}

		@Test
		public void notEntity() throws Exception {
			String beanName = "";
			Method method = ConstructorTestSource.NotEntity.class
					.getDeclaredMethod("handle", ConstructorTestSource.TestEntity3.class);
			BeanFactory beanFactory = mock(BeanFactory.class);
			assertThrows(IllegalArgumentException.class,
					() -> new DomaApplicationListener(beanName, method, beanFactory));
		}

		@Test
		public void invalidContextClass() throws Exception {
			String beanName = "";
			Method method = ConstructorTestSource.InvalidContextClass.class.getDeclaredMethod("handle",
					ConstructorTestSource.TestEntity1.class, PostInsertContext.class);
			BeanFactory beanFactory = mock(BeanFactory.class);
			assertThrows(IllegalArgumentException.class,
					() -> new DomaApplicationListener(beanName, method, beanFactory));
		}

		@Test
		public void invalidContextTypeVar() throws Exception {
			String beanName = "";
			Method method = ConstructorTestSource.InvalidContextTypeVar.class.getDeclaredMethod("handle",
					ConstructorTestSource.TestEntity1.class, PreInsertContext.class);
			BeanFactory beanFactory = mock(BeanFactory.class);
			assertThrows(IllegalArgumentException.class,
					() -> new DomaApplicationListener(beanName, method, beanFactory));
		}

		@Test
		public void noArg() throws Exception {
			String beanName = "";
			Method method = ConstructorTestSource.NoArg.class.getDeclaredMethod("handle");
			BeanFactory beanFactory = mock(BeanFactory.class);
			assertThrows(IllegalArgumentException.class,
					() -> new DomaApplicationListener(beanName, method, beanFactory));
		}

		@Test
		public void tooManyArgs() throws Exception {
			String beanName = "";
			Method method = ConstructorTestSource.TooManyArgs.class.getDeclaredMethod("handle",
					ConstructorTestSource.TestEntity1.class, PreInsertContext.class, Object.class);
			BeanFactory beanFactory = mock(BeanFactory.class);
			assertThrows(IllegalArgumentException.class,
					() -> new DomaApplicationListener(beanName, method, beanFactory));
		}

		@Test
		public void multiAnnotationsWithContext() throws Exception {
			String beanName = "";
			Method method = ConstructorTestSource.MultiAnnotationsWithContext.class.getDeclaredMethod("handle",
					ConstructorTestSource.TestEntity1.class, PreInsertContext.class);
			BeanFactory beanFactory = mock(BeanFactory.class);
			assertThrows(IllegalArgumentException.class,
					() -> new DomaApplicationListener(beanName, method, beanFactory));
		}

	}

	public static class ConstructorTestSource {

		@Entity
		public static class TestEntity1 {
		}

		@Entity
		public static class TestEntity2 {
		}

		public static class TestEntity3 {
		}

		static class EntityOnly {
			@HandlePreInsert
			void handle(TestEntity1 entity) {
			}
		}

		static class EntityWithContext {
			@HandlePreInsert
			void handle(TestEntity1 entity, PreInsertContext<TestEntity1> context) {
			}
		}

		static class MultiAnnotations {
			@HandlePreInsert
			@HandlePostInsert
			void handle(TestEntity1 entity) {
			}
		}

		static class NotEntity {
			@HandlePreInsert
			void handle(TestEntity3 entity) {
			}
		}

		static class InvalidContextClass {
			@HandlePreInsert
			void handle(TestEntity1 entity, PostInsertContext<TestEntity1> context) {
			}
		}

		static class InvalidContextTypeVar {
			@HandlePreInsert
			void handle(TestEntity1 entity, PreInsertContext<TestEntity2> context) {
			}
		}

		static class NoArg {
			@HandlePreInsert
			void handle() {
			}
		}

		static class TooManyArgs {
			@HandlePreInsert
			void handle(TestEntity1 entity, PreInsertContext<TestEntity1> context,
						Object unnecessary) {
			}
		}

		static class MultiAnnotationsWithContext {
			@HandlePreInsert
			@HandlePostInsert
			void handle(TestEntity1 entity, PreInsertContext<TestEntity1> context) {
			}
		}
	}

	@Nested
	public class OnApplicationEventTest {

		@Test
		public void handleEvent() throws Exception {
			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
			context.register(OnApplicationEventTestSource.EntityOnlyHandler.class);
			context.refresh();

			DomaApplicationListener listener = new DomaApplicationListener(
					"entityOnlyHandler", OnApplicationEventTestSource.EntityOnlyHandler.class.getDeclaredMethod(
					"handle", OnApplicationEventTestSource.TestEntity1.class),
					context);

			OnApplicationEventTestSource.TestEntity1 entity = new OnApplicationEventTestSource.TestEntity1();
			@SuppressWarnings("unchecked")
			PreInsertContext<OnApplicationEventTestSource.TestEntity1> ctx = mock(PreInsertContext.class);
			PreInsertEvent<OnApplicationEventTestSource.TestEntity1> event = new PreInsertEvent<>(entity, ctx);
			listener.onApplicationEvent(event);

			OnApplicationEventTestSource.EntityOnlyHandler handler =
					context.getBean(OnApplicationEventTestSource.EntityOnlyHandler.class);
			assertThat(handler.entity).isSameAs(entity);
		}

		@Test
		public void handleEventWithContext() throws Exception {
			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
			context.register(OnApplicationEventTestSource.WithContextHandler.class);

			// In default, maximum one parameter is allowed for event listener method.
			// So, use PassthroughEventListenerFactory
			context.register(OnApplicationEventTestSource.PassthroughEventListenerFactory.class);

			context.refresh();

			DomaApplicationListener listener = new DomaApplicationListener(
					"withContextHandler", OnApplicationEventTestSource.WithContextHandler.class.getDeclaredMethod(
					"handle", OnApplicationEventTestSource.TestEntity1.class, PreInsertContext.class),
					context);

			OnApplicationEventTestSource.TestEntity1 entity = new OnApplicationEventTestSource.TestEntity1();
			@SuppressWarnings("unchecked")
			PreInsertContext<OnApplicationEventTestSource.TestEntity1> ctx = mock(PreInsertContext.class);
			PreInsertEvent<OnApplicationEventTestSource.TestEntity1> event = new PreInsertEvent<>(entity, ctx);
			listener.onApplicationEvent(event);

			OnApplicationEventTestSource.WithContextHandler handler =
					context.getBean(OnApplicationEventTestSource.WithContextHandler.class);
			assertThat(handler.entity).isSameAs(entity);
			assertThat(handler.context).isSameAs(ctx);
		}

		@Test
		public void differentEventContext() throws Exception {
			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
			context.register(OnApplicationEventTestSource.EntityOnlyHandler.class);
			context.refresh();

			DomaApplicationListener listener = new DomaApplicationListener(
					"entityOnlyHandler", OnApplicationEventTestSource.EntityOnlyHandler.class.getDeclaredMethod(
					"handle", OnApplicationEventTestSource.TestEntity1.class),
					context);

			OnApplicationEventTestSource.TestEntity1 entity = new OnApplicationEventTestSource.TestEntity1();
			@SuppressWarnings("unchecked")
			PostInsertContext<OnApplicationEventTestSource.TestEntity1> ctx = mock(PostInsertContext.class);
			PostInsertEvent<OnApplicationEventTestSource.TestEntity1> event = new PostInsertEvent<>(entity, ctx);
			listener.onApplicationEvent(event);

			OnApplicationEventTestSource.EntityOnlyHandler handler =
					context.getBean(OnApplicationEventTestSource.EntityOnlyHandler.class);
			assertThat(handler.entity).isNull();
		}

		@Test
		public void differentEntity() throws Exception {
			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
			context.register(OnApplicationEventTestSource.EntityOnlyHandler.class);
			context.refresh();

			DomaApplicationListener listener = new DomaApplicationListener(
					"entityOnlyHandler", OnApplicationEventTestSource.EntityOnlyHandler.class.getDeclaredMethod(
					"handle", OnApplicationEventTestSource.TestEntity1.class),
					context);

			OnApplicationEventTestSource.TestEntity2 entity = new OnApplicationEventTestSource.TestEntity2();
			@SuppressWarnings("unchecked")
			PreInsertContext<OnApplicationEventTestSource.TestEntity2> ctx = mock(PreInsertContext.class);
			PreInsertEvent<OnApplicationEventTestSource.TestEntity2> event = new PreInsertEvent<>(entity, ctx);
			listener.onApplicationEvent(event);

			OnApplicationEventTestSource.EntityOnlyHandler handler =
					context.getBean(OnApplicationEventTestSource.EntityOnlyHandler.class);
			assertThat(handler.entity).isNull();
		}

		@Test
		public void handleSubEntity() throws Exception {
			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
			context.register(OnApplicationEventTestSource.SuperClassHandler.class);
			context.refresh();

			DomaApplicationListener listener = new DomaApplicationListener(
					"superClassHandler", OnApplicationEventTestSource.SuperClassHandler.class.getDeclaredMethod(
					"handle", OnApplicationEventTestSource.TestEntity1.class),
					context);

			OnApplicationEventTestSource.TestEntity3 entity = new OnApplicationEventTestSource.TestEntity3();
			@SuppressWarnings("unchecked")
			PreInsertContext<OnApplicationEventTestSource.TestEntity1> ctx = mock(PreInsertContext.class);
			PreInsertEvent<OnApplicationEventTestSource.TestEntity1> event = new PreInsertEvent<>(entity, ctx);
			listener.onApplicationEvent(event);

			OnApplicationEventTestSource.SuperClassHandler handler =
					context.getBean(OnApplicationEventTestSource.SuperClassHandler.class);
			assertThat(handler.entity).isSameAs(entity);
		}

	}

	public static class OnApplicationEventTestSource {

		@Entity
		public static class TestEntity1 {
		}

		@Entity
		public static class TestEntity2 {
		}

		@Entity
		public static class TestEntity3 extends TestEntity1 {
		}

		@Component("entityOnlyHandler")
		static class EntityOnlyHandler {
			TestEntity1 entity;

			@HandlePreInsert
			void handle(TestEntity1 entity) {
				this.entity = entity;
			}
		}

		@Component("withContextHandler")
		static class WithContextHandler {
			TestEntity1 entity;
			PreInsertContext<TestEntity1> context;

			@HandlePreInsert
			void handle(TestEntity1 entity, PreInsertContext<TestEntity1> context) {
				this.entity = entity;
				this.context = context;
			}
		}

		@Component("superClassHandler")
		static class SuperClassHandler {
			TestEntity1 entity;

			@HandlePreInsert
			void handle(TestEntity1 entity) {
				this.entity = entity;
			}
		}

		// java.lang.IllegalStateException
		// Maximum one parameter is allowed for event listener method
		static class PassthroughEventListenerFactory implements EventListenerFactory,
				Ordered {

			@Override
			public int getOrder() {
				return Ordered.HIGHEST_PRECEDENCE;
			}

			@Override
			public boolean supportsMethod(Method method) {
				return true;
			}

			@Override
			public ApplicationListener<?> createApplicationListener(String beanName,
																	Class<?> type, Method method) {
				return event -> {
				};
			}
		}
	}
}