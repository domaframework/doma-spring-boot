package org.seasar.doma.boot.event;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.seasar.doma.boot.event.annotation.HandlePostDelete;
import org.seasar.doma.boot.event.annotation.HandlePostInsert;
import org.seasar.doma.boot.event.annotation.HandlePostUpdate;
import org.seasar.doma.boot.event.annotation.HandlePreDelete;
import org.seasar.doma.boot.event.annotation.HandlePreInsert;
import org.seasar.doma.boot.event.annotation.HandlePreUpdate;
import org.seasar.doma.jdbc.entity.PostDeleteContext;
import org.seasar.doma.jdbc.entity.PostInsertContext;
import org.seasar.doma.jdbc.entity.PostUpdateContext;
import org.seasar.doma.jdbc.entity.PreDeleteContext;
import org.seasar.doma.jdbc.entity.PreInsertContext;
import org.seasar.doma.jdbc.entity.PreUpdateContext;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.EventListener;

@SuppressWarnings("unchecked")
public class DomaEventEntityListenerTest {
	@Test
	void handlePreInsert() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(DomaEventEntityListener.class);
			context.register(DomaEventListenerFactory.class);
			context.register(PreInsertHandler.class);
			context.refresh();

			DomaEventEntityListener<Entity> entityListener = context
					.getBean(DomaEventEntityListener.class);

			PreInsertContext<Entity> ctx = mock(PreInsertContext.class);
			Entity entity = new Entity();
			entityListener.preInsert(entity, ctx);
			PreInsertHandler handler = context.getBean(PreInsertHandler.class);
			assertThat(handler.entity).isSameAs(entity);
		}
	}

	@Test
	void handlePreInsertWithContext() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(DomaEventEntityListener.class);
			context.register(DomaEventListenerFactory.class);
			context.register(PreInsertHandlerWithContext.class);
			context.refresh();

			DomaEventEntityListener<Entity> entityListener = context
					.getBean(DomaEventEntityListener.class);

			PreInsertContext<Entity> ctx = mock(PreInsertContext.class);
			Entity entity = new Entity();
			entityListener.preInsert(entity, ctx);
			PreInsertHandlerWithContext handler = context
					.getBean(PreInsertHandlerWithContext.class);
			assertThat(handler.entity).isSameAs(entity);
			assertThat(handler.ctx).isSameAs(ctx);
		}
	}

	@Test
	void handlePreUpdate() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(DomaEventEntityListener.class);
			context.register(DomaEventListenerFactory.class);
			context.register(PreUpdateHandler.class);
			context.refresh();

			DomaEventEntityListener<Entity> entityListener = context
					.getBean(DomaEventEntityListener.class);

			PreUpdateContext<Entity> ctx = mock(PreUpdateContext.class);
			Entity entity = new Entity();
			entityListener.preUpdate(entity, ctx);
			PreUpdateHandler handler = context.getBean(PreUpdateHandler.class);
			assertThat(handler.entity).isSameAs(entity);
		}
	}

	@Test
	void handlePreUpdateWithContext() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(DomaEventEntityListener.class);
			context.register(DomaEventListenerFactory.class);
			context.register(PreUpdateHandlerWithContext.class);
			context.refresh();

			DomaEventEntityListener<Entity> entityListener = context
					.getBean(DomaEventEntityListener.class);

			PreUpdateContext<Entity> ctx = mock(PreUpdateContext.class);
			Entity entity = new Entity();
			entityListener.preUpdate(entity, ctx);
			PreUpdateHandlerWithContext handler = context
					.getBean(PreUpdateHandlerWithContext.class);
			assertThat(handler.entity).isSameAs(entity);
			assertThat(handler.ctx).isSameAs(ctx);
		}
	}

	@Test
	void handlePreDelete() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(DomaEventEntityListener.class);
			context.register(DomaEventListenerFactory.class);
			context.register(PreDeleteHandler.class);
			context.refresh();

			DomaEventEntityListener<Entity> entityListener = context
					.getBean(DomaEventEntityListener.class);

			PreDeleteContext<Entity> ctx = mock(PreDeleteContext.class);
			Entity entity = new Entity();
			entityListener.preDelete(entity, ctx);
			PreDeleteHandler handler = context.getBean(PreDeleteHandler.class);
			assertThat(handler.entity).isSameAs(entity);
		}
	}

	@Test
	public void handlePreDeleteWithContext() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(DomaEventEntityListener.class);
			context.register(DomaEventListenerFactory.class);
			context.register(PreDeleteHandlerWithContext.class);
			context.refresh();

			DomaEventEntityListener<Entity> entityListener = context
					.getBean(DomaEventEntityListener.class);

			PreDeleteContext<Entity> ctx = mock(PreDeleteContext.class);
			Entity entity = new Entity();
			entityListener.preDelete(entity, ctx);
			PreDeleteHandlerWithContext handler = context
					.getBean(PreDeleteHandlerWithContext.class);
			assertThat(handler.entity).isSameAs(entity);
			assertThat(handler.ctx).isSameAs(ctx);
		}
	}

	@Test
	public void handlePostInsert() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(DomaEventEntityListener.class);
			context.register(DomaEventListenerFactory.class);
			context.register(PostInsertHandler.class);
			context.refresh();

			DomaEventEntityListener<Entity> entityListener = context
					.getBean(DomaEventEntityListener.class);

			PostInsertContext<Entity> ctx = mock(PostInsertContext.class);
			Entity entity = new Entity();
			entityListener.postInsert(entity, ctx);
			PostInsertHandler handler = context.getBean(PostInsertHandler.class);
			assertThat(handler.entity).isSameAs(entity);
		}
	}

	@Test
	public void handlePostInsertWithContext() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(DomaEventEntityListener.class);
			context.register(DomaEventListenerFactory.class);
			context.register(PostInsertHandlerWithContext.class);
			context.refresh();

			DomaEventEntityListener<Entity> entityListener = context
					.getBean(DomaEventEntityListener.class);

			PostInsertContext<Entity> ctx = mock(PostInsertContext.class);
			Entity entity = new Entity();
			entityListener.postInsert(entity, ctx);
			PostInsertHandlerWithContext handler = context
					.getBean(PostInsertHandlerWithContext.class);
			assertThat(handler.entity).isSameAs(entity);
			assertThat(handler.ctx).isSameAs(ctx);
		}
	}

	@Test
	public void handlePostUpdate() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(DomaEventEntityListener.class);
			context.register(DomaEventListenerFactory.class);
			context.register(PostUpdateHandler.class);
			context.refresh();

			DomaEventEntityListener<Entity> entityListener = context
					.getBean(DomaEventEntityListener.class);

			PostUpdateContext<Entity> ctx = mock(PostUpdateContext.class);
			Entity entity = new Entity();
			entityListener.postUpdate(entity, ctx);
			PostUpdateHandler handler = context.getBean(PostUpdateHandler.class);
			assertThat(handler.entity).isSameAs(entity);
		}
	}

	@Test
	public void handlePostUpdateWithContext() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(DomaEventEntityListener.class);
			context.register(DomaEventListenerFactory.class);
			context.register(PostUpdateHandlerWithContext.class);
			context.refresh();

			DomaEventEntityListener<Entity> entityListener = context
					.getBean(DomaEventEntityListener.class);

			PostUpdateContext<Entity> ctx = mock(PostUpdateContext.class);
			Entity entity = new Entity();
			entityListener.postUpdate(entity, ctx);
			PostUpdateHandlerWithContext handler = context
					.getBean(PostUpdateHandlerWithContext.class);
			assertThat(handler.entity).isSameAs(entity);
			assertThat(handler.ctx).isSameAs(ctx);
		}
	}

	@Test
	public void handlePostDelete() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(DomaEventEntityListener.class);
			context.register(DomaEventListenerFactory.class);
			context.register(PostDeleteHandler.class);
			context.refresh();

			DomaEventEntityListener<Entity> entityListener = context
					.getBean(DomaEventEntityListener.class);

			PostDeleteContext<Entity> ctx = mock(PostDeleteContext.class);
			Entity entity = new Entity();
			entityListener.postDelete(entity, ctx);
			PostDeleteHandler handler = context.getBean(PostDeleteHandler.class);
			assertThat(handler.entity).isSameAs(entity);
		}
	}

	@Test
	public void handlePostDeleteWithContext() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(DomaEventEntityListener.class);
			context.register(DomaEventListenerFactory.class);
			context.register(PostDeleteHandlerWithContext.class);
			context.refresh();

			DomaEventEntityListener<Entity> entityListener = context
					.getBean(DomaEventEntityListener.class);

			PostDeleteContext<Entity> ctx = mock(PostDeleteContext.class);
			Entity entity = new Entity();
			entityListener.postDelete(entity, ctx);
			PostDeleteHandlerWithContext handler = context
					.getBean(PostDeleteHandlerWithContext.class);
			assertThat(handler.entity).isSameAs(entity);
			assertThat(handler.ctx).isSameAs(ctx);
		}
	}

	@Test
	public void anotherEntity() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(DomaEventEntityListener.class);
			context.register(DomaEventListenerFactory.class);
			context.register(PreInsertHandler.class);
			context.register(PreInsertHandler2.class);
			context.refresh();

			DomaEventEntityListener<Entity2> entityListener = context
					.getBean(DomaEventEntityListener.class);

			PreInsertContext<Entity2> ctx = mock(PreInsertContext.class);
			Entity2 entity = new Entity2();
			entityListener.preInsert(entity, ctx);

			PreInsertHandler handler = context.getBean(PreInsertHandler.class);
			assertThat(handler.entity).isNull();

			PreInsertHandler2 handler2 = context.getBean(PreInsertHandler2.class);
			assertThat(handler2.entity).isSameAs(entity);
		}
	}

	@Test
	public void multiHandlers() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(DomaEventEntityListener.class);
			context.register(DomaEventListenerFactory.class);
			context.register(PrePostInsertHandler.class);
			context.refresh();

			DomaEventEntityListener<Entity> entityListener = context
					.getBean(DomaEventEntityListener.class);
			PrePostInsertHandler handler = context.getBean(PrePostInsertHandler.class);

			PreInsertContext<Entity> preCtx = mock(PreInsertContext.class);
			PostInsertContext<Entity> postCtx = mock(PostInsertContext.class);

			Entity entity = new Entity();
			entityListener.preInsert(entity, preCtx);
			assertThat(handler.preEntity).isSameAs(entity);
			entityListener.postInsert(entity, postCtx);
			assertThat(handler.postEntity).isSameAs(entity);
		}
	}

	@Test
	public void multiAnnotations() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(DomaEventEntityListener.class);
			context.register(DomaEventListenerFactory.class);
			context.register(InsertHandler.class);
			context.refresh();

			DomaEventEntityListener<Entity> entityListener = context
					.getBean(DomaEventEntityListener.class);
			InsertHandler handler = context.getBean(InsertHandler.class);

			PreInsertContext<Entity> preCtx = mock(PreInsertContext.class);
			PostInsertContext<Entity> postCtx = mock(PostInsertContext.class);

			Entity entity = new Entity();
			entityListener.preInsert(entity, preCtx);
			assertThat(handler.entity).isSameAs(entity);
			Entity entity2 = new Entity();
			entityListener.postInsert(entity2, postCtx);
			assertThat(handler.entity).isSameAs(entity2);
		}
	}

	@Test
	public void noArg() throws Exception {
		assertThrows(BeanInitializationException.class, () -> {
			try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
				context.register(DomaEventEntityListener.class);
				context.register(DomaEventListenerFactory.class);
				context.register(NoArgHandler.class);
				context.refresh();
			}
		});
	}

	@Test
	public void springEventListener() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(DomaEventEntityListener.class);
			context.register(DomaEventListenerFactory.class);
			context.register(SpringListener.class);
			context.register(PreInsertHandler.class);
			context.refresh();

			DomaEventEntityListener<Entity> entityListener = context
					.getBean(DomaEventEntityListener.class);
			SpringListener springListener = context.getBean(SpringListener.class);

			PreInsertContext<Entity> ctx = mock(PreInsertContext.class);
			Entity entity = new Entity();
			entityListener.preInsert(entity, ctx);
			DomaEvent<Entity, PreInsertContext<Entity>> event = springListener.event;
			assertThat(event).isNotNull();
			assertThat(event).isInstanceOf(PreInsertEvent.class);
			assertThat(event.getSource()).isSameAs(entity);
			assertThat(event.getContext()).isSameAs(ctx);
		}
	}

	@Test
	public void springConditionalEventListener() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(DomaEventEntityListener.class);
			context.register(DomaEventListenerFactory.class);
			context.register(TodoListener.class);
			context.register(PreInsertHandler.class);
			context.refresh();

			DomaEventEntityListener<Todo> entityListener = context
					.getBean(DomaEventEntityListener.class);
			TodoListener todoListener = context.getBean(TodoListener.class);

			PreInsertContext<Todo> ctx = mock(PreInsertContext.class);
			Todo entity = new Todo();

			entity.createdBy = "someone";
			entityListener.preInsert(entity, ctx);
			Todo todo = todoListener.todo;
			assertThat(todo).isNull();

			entity.createdBy = "making";
			entityListener.preInsert(entity, ctx);
			todo = todoListener.todo;
			assertThat(todo).isNotNull();
			assertThat(todo).isSameAs(entity);
		}
	}

	@org.seasar.doma.Entity
	public static class Entity {

	}

	@org.seasar.doma.Entity
	public static class Entity2 {

	}

	static class PreInsertHandler {
		Entity entity;

		@HandlePreInsert
		public void handlePreInsert(Entity entity) {
			this.entity = entity;
		}
	}

	static class PreInsertHandler2 {
		Entity2 entity;

		@HandlePreInsert
		public void handlePreInsert(Entity2 entity) {
			this.entity = entity;
		}
	}

	static class PreInsertHandlerWithContext {
		Entity entity;
		PreInsertContext<Entity> ctx;

		@HandlePreInsert
		public void handlePreInsert(Entity entity, PreInsertContext<Entity> ctx) {
			this.entity = entity;
			this.ctx = ctx;
		}
	}

	static class PreUpdateHandler {
		Entity entity;

		@HandlePreUpdate
		public void handlePreUpdate(Entity entity) {
			this.entity = entity;
		}
	}

	static class PreUpdateHandlerWithContext {
		Entity entity;
		PreUpdateContext<Entity> ctx;

		@HandlePreUpdate
		public void handlePreUpdate(Entity entity, PreUpdateContext<Entity> ctx) {
			this.entity = entity;
			this.ctx = ctx;
		}
	}

	static class PreDeleteHandler {
		Entity entity;

		@HandlePreDelete
		public void handlePreDelete(Entity entity) {
			this.entity = entity;
		}
	}

	static class PreDeleteHandlerWithContext {
		Entity entity;
		PreDeleteContext<Entity> ctx;

		@HandlePreDelete
		public void handlePreDelete(Entity entity, PreDeleteContext<Entity> ctx) {
			this.entity = entity;
			this.ctx = ctx;
		}
	}

	static class PostInsertHandler {
		Entity entity;

		@HandlePostInsert
		public void handlePostInsert(Entity entity) {
			this.entity = entity;
		}
	}

	static class PostInsertHandlerWithContext {
		Entity entity;
		PostInsertContext<Entity> ctx;

		@HandlePostInsert
		public void handlePostInsert(Entity entity, PostInsertContext<Entity> ctx) {
			this.entity = entity;
			this.ctx = ctx;
		}
	}

	static class PostUpdateHandler {
		Entity entity;

		@HandlePostUpdate
		public void handlePostUpdate(Entity entity) {
			this.entity = entity;
		}
	}

	static class PostUpdateHandlerWithContext {
		Entity entity;
		PostUpdateContext<Entity> ctx;

		@HandlePostUpdate
		public void handlePostUpdate(Entity entity, PostUpdateContext<Entity> ctx) {
			this.entity = entity;
			this.ctx = ctx;
		}
	}

	static class PostDeleteHandler {
		Entity entity;

		@HandlePostDelete
		public void handlePostDelete(Entity entity) {
			this.entity = entity;
		}
	}

	static class PostDeleteHandlerWithContext {
		Entity entity;
		PostDeleteContext<Entity> ctx;

		@HandlePostDelete
		public void handlePostDelete(Entity entity, PostDeleteContext<Entity> ctx) {
			this.entity = entity;
			this.ctx = ctx;
		}
	}

	static class PrePostInsertHandler {
		Entity preEntity;
		Entity postEntity;

		@HandlePreInsert
		public void handlePreInsert(Entity entity) {
			this.preEntity = entity;
		}

		@HandlePostInsert
		public void handlePostInsert(Entity entity) {
			this.postEntity = entity;
		}
	}

	static class InsertHandler {
		Entity entity;

		@HandlePreInsert
		@HandlePostInsert
		public void handleInsert(Entity entity) {
			this.entity = entity;
		}
	}

	static class NoArgHandler {
		@HandlePreInsert
		public void noarg() {
		}
	}

	static class SpringListener {
		DomaEvent<Entity, PreInsertContext<Entity>> event;

		@EventListener
		public void listen(DomaEvent<Entity, PreInsertContext<Entity>> event) {
			this.event = event;
		}
	}

	@org.seasar.doma.Entity
	public static class Todo {
		String createdBy;

		public String getCreatedBy() {
			return createdBy;
		}
	}

	static class TodoListener {
		Todo todo;

		@EventListener(condition = "#root.event.source.createdBy == 'making'")
		public void handlePreInsert(PreInsertEvent<Todo> event) {
			this.todo = event.getSource();
		}
	}
}
