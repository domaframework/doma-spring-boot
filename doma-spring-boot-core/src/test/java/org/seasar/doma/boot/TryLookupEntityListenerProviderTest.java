/*
 * Copyright (C) 2004-2016 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
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
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(FooListener.class);
		context.refresh();
		TryLookupEntityListenerProvider provider = new TryLookupEntityListenerProvider();
		provider.setApplicationContext(context);
		FooListener listener = provider.get(FooListener.class, FooListener::new);
		assertThat(listener.managed, is(true));
	}

	@Test(expected = IllegalStateException.class)
	public void testManaged_notUnique() throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				FooConfig.class);
		TryLookupEntityListenerProvider provider = new TryLookupEntityListenerProvider();
		provider.setApplicationContext(context);
		provider.get(FooListener.class, FooListener::new);
	}

	@Test
	public void testNotManaged() throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.refresh();
		TryLookupEntityListenerProvider provider = new TryLookupEntityListenerProvider();
		provider.setApplicationContext(context);
		FooListener listener = provider.get(FooListener.class, FooListener::new);
		assertThat(listener.managed, is(false));
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
