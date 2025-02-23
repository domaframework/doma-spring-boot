package org.seasar.doma.boot;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URL;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class ResourceLoaderScriptFileLoaderTest {

	@Test
	void testLoadAsURL() throws Exception {
		var location = "META-INF/com/example/dao/TestDao/test.script";
		var expectedURL = new URL("file:///" + location);
		var resourceLoader = mock(ResourceLoader.class);
		var resource = mock(Resource.class);

		when(resourceLoader.getResource(ResourceLoader.CLASSPATH_URL_PREFIX + location))
				.thenReturn(resource);
		when(resource.exists()).thenReturn(true);
		when(resource.getURL()).thenReturn(expectedURL);

		var sut = new ResourceLoaderScriptFileLoader(resourceLoader);
		var actualURL = sut.loadAsURL(location);
		assertEquals(expectedURL, actualURL);
	}

	@Test
	void testLoadAsURLFallback() throws Exception {
		var location = "META-INF/com/example/dao/TestDao/test.script";
		var expectedURL = new URL("file:///" + location);
		var resourceLoader = mock(ResourceLoader.class);
		var resource = mock(Resource.class);
		var notExistsResource = mock(Resource.class);

		when(resourceLoader.getResource(ResourceLoader.CLASSPATH_URL_PREFIX + location))
				.thenReturn(notExistsResource);
		when(resourceLoader.getResource(ResourceLoader.CLASSPATH_URL_PREFIX + "/" + location))
				.thenReturn(resource);
		when(notExistsResource.exists()).thenReturn(false);
		when(resource.exists()).thenReturn(true);
		when(resource.getURL()).thenReturn(expectedURL);

		var sut = new ResourceLoaderScriptFileLoader(resourceLoader);
		var actualURL = sut.loadAsURL(location);
		assertEquals(expectedURL, actualURL);
	}

	@Test
	void testLoadAsURLScriptNotFound() {
		var location = "META-INF/com/example/dao/TestDao/test.script";
		var resourceLoader = mock(ResourceLoader.class);
		var notExistsResource = mock(Resource.class);

		when(resourceLoader.getResource(ResourceLoader.CLASSPATH_URL_PREFIX + location))
				.thenReturn(notExistsResource);
		when(resourceLoader.getResource(ResourceLoader.CLASSPATH_URL_PREFIX + "/" + location))
				.thenReturn(notExistsResource);
		when(notExistsResource.exists()).thenReturn(false);

		var sut = new ResourceLoaderScriptFileLoader(resourceLoader);
		var actualURL = sut.loadAsURL(location);
		assertNull(actualURL);
	}
}
