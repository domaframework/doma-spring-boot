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

import java.util.Map;
import java.util.function.Supplier;

import org.seasar.doma.jdbc.EntityListenerProvider;
import org.seasar.doma.jdbc.entity.EntityListener;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * {@link EntityListenerProvider} implementation that
 * {@link EntityListener} managed by Spring Framework, or else created by Doma.
 * 
 * @author backpaper0
 *
 */
class TryLookupEntityListenerProvider implements EntityListenerProvider, ApplicationContextAware {

	private ApplicationContext context;
	
	@Override
	public <ENTITY, LISTENER extends EntityListener<ENTITY>> LISTENER get(
			Class<LISTENER> listenerClass, Supplier<LISTENER> listenerSupplier) {
		Map<String, LISTENER> beans = context.getBeansOfType(listenerClass);
		if (beans.size() > 1) {
			throw new IllegalStateException(
					"Bean type of " + listenerClass + " bean must be unique!");
		}
		return beans.values().stream().findAny().orElseGet(listenerSupplier);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}
}
