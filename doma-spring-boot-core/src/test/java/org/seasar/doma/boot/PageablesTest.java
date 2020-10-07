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

import org.junit.Test;
import org.seasar.doma.jdbc.SelectOptions;
import org.seasar.doma.jdbc.SelectOptionsAccessor;
import org.springframework.data.domain.PageRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PageablesTest {

	@Test
	public void testToSelectOptions() throws Exception {
		SelectOptions options = Pageables.toSelectOptions(pageRequest(0, 10));
		assertThat(SelectOptionsAccessor.getOffset(options), is(0L));
		assertThat(SelectOptionsAccessor.getLimit(options), is(10L));
	}

	@Test
	public void testToSelectOptions2() throws Exception {
		SelectOptions options = Pageables.toSelectOptions(pageRequest(2, 10));
		assertThat(SelectOptionsAccessor.getOffset(options), is(20L));
		assertThat(SelectOptionsAccessor.getLimit(options), is(10L));
	}

	@Test
	public void testToSelectOptions3() throws Exception {
		SelectOptions options = Pageables.toSelectOptions(pageRequest(2, 5));
		assertThat(SelectOptionsAccessor.getOffset(options), is(10L));
		assertThat(SelectOptionsAccessor.getLimit(options), is(5L));
	}

	private static PageRequest pageRequest(int page, int size) throws Exception {
		try {
			// Try PageRequest.of(int, int) added since Spring Data Commons 2.0
			return (PageRequest) PageRequest.class.getMethod("of", int.class, int.class)
					.invoke(null, page, size);
		} catch (NoSuchMethodException e) {
			// If 'of' method is missing (In other words, Spring Data Commons version is
			// less than 2.0),
			// then it use constructor with two int arguments.
			return PageRequest.class.getConstructor(int.class, int.class).newInstance(
					page, size);
		}
	}
}
