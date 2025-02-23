package org.seasar.doma.boot;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

import org.seasar.doma.jdbc.ScriptFileLoader;
import org.springframework.core.io.ResourceLoader;

public class ResourceLoaderScriptFileLoader implements ScriptFileLoader {

	private final ResourceLoader resourceLoader;

	public ResourceLoaderScriptFileLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public URL loadAsURL(String path) {
		try {
			var resource = resourceLoader.getResource(ResourceLoader.CLASSPATH_URL_PREFIX + path);
			if (resource.exists()) {
				return resource.getURL();
			}
			resource = resourceLoader.getResource(ResourceLoader.CLASSPATH_URL_PREFIX + "/" + path);
			if (resource.exists()) {
				return resource.getURL();
			}
			return null;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
