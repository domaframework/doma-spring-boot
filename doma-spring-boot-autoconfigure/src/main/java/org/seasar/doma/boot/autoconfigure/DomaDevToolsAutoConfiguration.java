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
package org.seasar.doma.boot.autoconfigure;

import org.seasar.doma.jdbc.ClassHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.devtools.filewatch.FileSystemWatcherFactory;
import org.springframework.boot.devtools.restart.ConditionalOnInitializedRestarter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Autoconfigure to use Doma with devtools
 *
 * @author Toshiaki Maki
 */
@Configuration
@ConditionalOnClass(FileSystemWatcherFactory.class)
@ConditionalOnInitializedRestarter
public class DomaDevToolsAutoConfiguration {

	@Bean
	@ConditionalOnProperty(prefix = "spring.devtools.restart", name = "enabled", matchIfMissing = true)
	@ConditionalOnMissingBean
	public ClassHelper classHelper() {
		return new RestartAwareClassHelper();
	}

	/**
	 * ClassHelper to tell Doma
	 * {@link org.springframework.boot.devtools.restart.classloader.RestartClassLoader}.
	 */
	static class RestartAwareClassHelper implements ClassHelper {
		@SuppressWarnings("unchecked")
		@Override
		public <T> Class<T> forName(String className) throws Exception {
			return (Class<T>) Thread.currentThread().getContextClassLoader()
					.loadClass(className);
		}
	}
}
