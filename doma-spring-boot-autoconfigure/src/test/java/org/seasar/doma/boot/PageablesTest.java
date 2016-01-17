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
		SelectOptions options = Pageables.toSelectOptions(new PageRequest(0, 10));
		assertThat(SelectOptionsAccessor.getOffset(options), is(0L));
		assertThat(SelectOptionsAccessor.getLimit(options), is(10L));
	}

	@Test
	public void testToSelectOptions2() throws Exception {
		SelectOptions options = Pageables.toSelectOptions(new PageRequest(2, 10));
		assertThat(SelectOptionsAccessor.getOffset(options), is(20L));
		assertThat(SelectOptionsAccessor.getLimit(options), is(10L));
	}

	@Test
	public void testToSelectOptions3() throws Exception {
		SelectOptions options = Pageables.toSelectOptions(new PageRequest(2, 5));
		assertThat(SelectOptionsAccessor.getOffset(options), is(10L));
		assertThat(SelectOptionsAccessor.getLimit(options), is(5L));
	}
}
