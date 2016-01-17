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

import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.data.domain.Pageable;

/**
 * Converts Utilities for {@link Pageable} to be used with Doma.
 *
 * @author Toshiaki Maki
 */
public final class Pageables {
	/**
	 * Converts {@link Pageable} to {@link SelectOptions}
	 *
	 * @param pageable {@link Pageable} object to convert
	 * @return {@link SelectOptions} object corresponds to the given {@link Pageable}
	 * object.
	 */
	public static SelectOptions toSelectOptions(Pageable pageable) {
		final int offset = pageable.getPageNumber() * pageable.getPageSize();
		final int limit = pageable.getPageSize();
		return SelectOptions.get().offset(offset).limit(limit);
	}
}
