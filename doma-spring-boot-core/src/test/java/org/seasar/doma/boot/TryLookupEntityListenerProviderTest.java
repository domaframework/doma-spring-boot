package org.seasar.doma.boot;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;
import org.seasar.doma.jdbc.entity.EntityListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

public class TryLookupEntityListenerProviderTest {

	@Test
	public void testManaged() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(FooListener.class);
			context.refresh();
			TryLookupEntityListenerProvider provider = new TryLookupEntityListenerProvider();
			provider.setApplicationContext(context);
			FooListener listener = provider.get(FooListener.class, FooListener::new);
			assertThat(listener.managed, is(true));
		}
	}

	@Test(expected = IllegalStateException.class)
	public void testManaged_notUnique() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				FooConfig.class)) {
			TryLookupEntityListenerProvider provider = new TryLookupEntityListenerProvider();
			provider.setApplicationContext(context);
			provider.get(FooListener.class, FooListener::new);
		}
	}

	@Test
	public void testNotManaged() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.refresh();
			TryLookupEntityListenerProvider provider = new TryLookupEntityListenerProvider();
			provider.setApplicationContext(context);
			FooListener listener = provider.get(FooListener.class, FooListener::new);
			assertThat(listener.managed, is(false));
		}
	}

	@Component
	public static class FooListener implements EntityListener<Object> {

		final boolean managed;

		// Invoked by Doma
		public FooListener() {
			managed = false;
		}

		// Invoked by Spring
		@Autowired
		public FooListener(ApplicationContext context) {
			managed = true;
		}
	}

	public static class FooConfig {
		@Bean
		FooListener foo1() {
			return new FooListener();
		}

		@Bean
		FooListener foo2() {
			return new FooListener();
		}
	}
}
