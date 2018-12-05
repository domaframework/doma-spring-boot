package org.seasar.doma.boot.event;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.lang.reflect.Method;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
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

@RunWith(Enclosed.class)
public class DomaApplicationListenerTest {

	public static class ConstructorTest {

		@Rule
		public ExpectedException ee = ExpectedException.none();

		@Test
		public void entityOnly() throws Exception {
			String beanName = "";
			Method method = EntityOnly.class.getDeclaredMethod("handle",
					TestEntity1.class);
			BeanFactory beanFactory = mock(BeanFactory.class);
			new DomaApplicationListener(beanName, method, beanFactory);
		}

		@Test
		public void entityWithContext() throws Exception {
			String beanName = "";
			Method method = EntityWithContext.class.getDeclaredMethod("handle",
					TestEntity1.class, PreInsertContext.class);
			BeanFactory beanFactory = mock(BeanFactory.class);
			new DomaApplicationListener(beanName, method, beanFactory);
		}

		@Test
		public void multiAnnotations() throws Exception {
			String beanName = "";
			Method method = MultiAnnotations.class.getDeclaredMethod("handle",
					TestEntity1.class);
			BeanFactory beanFactory = mock(BeanFactory.class);
			new DomaApplicationListener(beanName, method, beanFactory);
		}

		@Test
		public void notEntity() throws Exception {
			ee.expect(IllegalArgumentException.class);
			String beanName = "";
			Method method = NotEntity.class
					.getDeclaredMethod("handle", TestEntity3.class);
			BeanFactory beanFactory = mock(BeanFactory.class);
			new DomaApplicationListener(beanName, method, beanFactory);
		}

		@Test
		public void invalidContextClass() throws Exception {
			ee.expect(IllegalArgumentException.class);
			String beanName = "";
			Method method = InvalidContextClass.class.getDeclaredMethod("handle",
					TestEntity1.class, PostInsertContext.class);
			BeanFactory beanFactory = mock(BeanFactory.class);
			new DomaApplicationListener(beanName, method, beanFactory);
		}

		@Test
		public void invalidContextTypeVar() throws Exception {
			ee.expect(IllegalArgumentException.class);
			String beanName = "";
			Method method = InvalidContextTypeVar.class.getDeclaredMethod("handle",
					TestEntity1.class, PreInsertContext.class);
			BeanFactory beanFactory = mock(BeanFactory.class);
			new DomaApplicationListener(beanName, method, beanFactory);
		}

		@Test
		public void noArg() throws Exception {
			ee.expect(IllegalArgumentException.class);
			String beanName = "";
			Method method = NoArg.class.getDeclaredMethod("handle");
			BeanFactory beanFactory = mock(BeanFactory.class);
			new DomaApplicationListener(beanName, method, beanFactory);
		}

		@Test
		public void tooManyArgs() throws Exception {
			ee.expect(IllegalArgumentException.class);
			String beanName = "";
			Method method = TooManyArgs.class.getDeclaredMethod("handle",
					TestEntity1.class, PreInsertContext.class, Object.class);
			BeanFactory beanFactory = mock(BeanFactory.class);
			new DomaApplicationListener(beanName, method, beanFactory);
		}

		@Test
		public void multiAnnotationsWithContext() throws Exception {
			ee.expect(IllegalArgumentException.class);
			String beanName = "";
			Method method = MultiAnnotationsWithContext.class.getDeclaredMethod("handle",
					TestEntity1.class, PreInsertContext.class);
			BeanFactory beanFactory = mock(BeanFactory.class);
			new DomaApplicationListener(beanName, method, beanFactory);
		}

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

	public static class OnApplicationEventTest {

		@Test
		public void handleEvent() throws Exception {
			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
			context.register(EntityOnlyHandler.class);
			context.refresh();

			DomaApplicationListener listener = new DomaApplicationListener(
					"entityOnlyHandler", EntityOnlyHandler.class.getDeclaredMethod(
							"handle", TestEntity1.class), context);

			TestEntity1 entity = new TestEntity1();
			PreInsertContext<TestEntity1> ctx = mock(PreInsertContext.class);
			PreInsertEvent<TestEntity1> event = new PreInsertEvent<>(entity, ctx);
			listener.onApplicationEvent(event);

			EntityOnlyHandler handler = context.getBean(EntityOnlyHandler.class);
			assertThat(handler.entity).isSameAs(entity);
		}

		@Test
		public void handleEventWithContext() throws Exception {
			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
			context.register(WithContextHandler.class);

			// In default, maximum one parameter is allowed for event listener method.
			// So, use PassthroughEventListenerFactory
			context.register(PassthroughEventListenerFactory.class);

			context.refresh();

			DomaApplicationListener listener = new DomaApplicationListener(
					"withContextHandler", WithContextHandler.class.getDeclaredMethod(
							"handle", TestEntity1.class, PreInsertContext.class), context);

			TestEntity1 entity = new TestEntity1();
			PreInsertContext<TestEntity1> ctx = mock(PreInsertContext.class);
			PreInsertEvent<TestEntity1> event = new PreInsertEvent<>(entity, ctx);
			listener.onApplicationEvent(event);

			WithContextHandler handler = context.getBean(WithContextHandler.class);
			assertThat(handler.entity).isSameAs(entity);
			assertThat(handler.context).isSameAs(ctx);
		}

		@Test
		public void differentEventContext() throws Exception {
			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
			context.register(EntityOnlyHandler.class);
			context.refresh();

			DomaApplicationListener listener = new DomaApplicationListener(
					"entityOnlyHandler", EntityOnlyHandler.class.getDeclaredMethod(
							"handle", TestEntity1.class), context);

			TestEntity1 entity = new TestEntity1();
			PostInsertContext<TestEntity1> ctx = mock(PostInsertContext.class);
			PostInsertEvent<TestEntity1> event = new PostInsertEvent<>(entity, ctx);
			listener.onApplicationEvent(event);

			EntityOnlyHandler handler = context.getBean(EntityOnlyHandler.class);
			assertThat(handler.entity).isNull();
		}

		@Test
		public void differentEntity() throws Exception {
			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
			context.register(EntityOnlyHandler.class);
			context.refresh();

			DomaApplicationListener listener = new DomaApplicationListener(
					"entityOnlyHandler", EntityOnlyHandler.class.getDeclaredMethod(
							"handle", TestEntity1.class), context);

			TestEntity2 entity = new TestEntity2();
			PreInsertContext<TestEntity2> ctx = mock(PreInsertContext.class);
			PreInsertEvent<TestEntity2> event = new PreInsertEvent<>(entity, ctx);
			listener.onApplicationEvent(event);

			EntityOnlyHandler handler = context.getBean(EntityOnlyHandler.class);
			assertThat(handler.entity).isNull();
		}

		@Test
		public void handleSubEntity() throws Exception {
			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
			context.register(SuperClassHandler.class);
			context.refresh();

			DomaApplicationListener listener = new DomaApplicationListener(
					"superClassHandler", SuperClassHandler.class.getDeclaredMethod(
							"handle", TestEntity1.class), context);

			TestEntity3 entity = new TestEntity3();
			PreInsertContext<TestEntity1> ctx = mock(PreInsertContext.class);
			PreInsertEvent<TestEntity1> event = new PreInsertEvent<>(entity, ctx);
			listener.onApplicationEvent(event);

			SuperClassHandler handler = context.getBean(SuperClassHandler.class);
			assertThat(handler.entity).isSameAs(entity);
		}

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