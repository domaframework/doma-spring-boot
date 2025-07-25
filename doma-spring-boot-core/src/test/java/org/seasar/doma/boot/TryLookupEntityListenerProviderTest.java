package org.seasar.doma.boot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.seasar.doma.jdbc.entity.EntityListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

class TryLookupEntityListenerProviderTest {

	@Test
	void managed() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(FooListener.class);
			context.refresh();
			TryLookupEntityListenerProvider provider = new TryLookupEntityListenerProvider();
			provider.setApplicationContext(context);
			FooListener listener = provider.get(FooListener.class, FooListener::new);
			assertThat(listener.managed).isTrue();
		}
	}

	@Test
	void managedNotUnique() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				FooConfig.class)) {
			TryLookupEntityListenerProvider provider = new TryLookupEntityListenerProvider();
			provider.setApplicationContext(context);
			assertThatExceptionOfType(IllegalStateException.class)
					.isThrownBy(() -> provider.get(FooListener.class, FooListener::new));
		}
	}

	@Test
	void notManaged() throws Exception {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.refresh();
			TryLookupEntityListenerProvider provider = new TryLookupEntityListenerProvider();
			provider.setApplicationContext(context);
			FooListener listener = provider.get(FooListener.class, FooListener::new);
			assertThat(listener.managed).isFalse();
		}
	}

	@Component
	static class FooListener implements EntityListener<Object> {

		final boolean managed;

		// Invoked by Doma
		FooListener() {
			managed = false;
		}

		// Invoked by Spring
		@Autowired
		FooListener(ApplicationContext context) {
			managed = true;
		}
	}

	static class FooConfig {
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
