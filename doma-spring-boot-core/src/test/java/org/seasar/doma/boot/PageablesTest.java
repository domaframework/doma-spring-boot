package org.seasar.doma.boot;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.seasar.doma.jdbc.SelectOptions;
import org.seasar.doma.jdbc.SelectOptionsAccessor;
import org.springframework.data.domain.PageRequest;

class PageablesTest {

	@Test
	void testToSelectOptions() throws Exception {
		SelectOptions options = Pageables.toSelectOptions(pageRequest(0, 10));
		assertEquals(0L, SelectOptionsAccessor.getOffset(options));
		assertEquals(10L, SelectOptionsAccessor.getLimit(options));
	}

	@Test
	void testToSelectOptions2() throws Exception {
		SelectOptions options = Pageables.toSelectOptions(pageRequest(2, 10));
		assertEquals(20L, SelectOptionsAccessor.getOffset(options));
		assertEquals(10L, SelectOptionsAccessor.getLimit(options));
	}

	@Test
	void testToSelectOptions3() throws Exception {
		SelectOptions options = Pageables.toSelectOptions(pageRequest(2, 5));
		assertEquals(10L, SelectOptionsAccessor.getOffset(options));
		assertEquals(5L, SelectOptionsAccessor.getLimit(options));
	}

	private static PageRequest pageRequest(int page, int size) throws Exception {
		try {
			// Try PageRequest.of(int, int) added since Spring Data Commons 2.0
			return (PageRequest) PageRequest.class.getMethod("of", int.class, int.class)
					.invoke(null, page, size);
		} catch (@SuppressWarnings("unused") NoSuchMethodException e) {
			// If 'of' method is missing (In other words, Spring Data Commons version is
			// less than 2.0),
			// then it use constructor with two int arguments.
			return PageRequest.class.getConstructor(int.class, int.class).newInstance(
					page, size);
		}
	}
}
