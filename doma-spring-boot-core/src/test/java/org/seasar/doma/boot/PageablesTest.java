package org.seasar.doma.boot;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.seasar.doma.jdbc.SelectOptions;
import org.seasar.doma.jdbc.SelectOptionsAccessor;
import org.springframework.data.domain.PageRequest;

class PageablesTest {

	@Test
	void toSelectOptions() throws Exception {
		SelectOptions options = Pageables.toSelectOptions(pageRequest(0, 10));
		assertThat(SelectOptionsAccessor.getOffset(options)).isEqualTo(0L);
		assertThat(SelectOptionsAccessor.getLimit(options)).isEqualTo(10L);
	}

	@Test
	void toSelectOptions2() throws Exception {
		SelectOptions options = Pageables.toSelectOptions(pageRequest(2, 10));
		assertThat(SelectOptionsAccessor.getOffset(options)).isEqualTo(20L);
		assertThat(SelectOptionsAccessor.getLimit(options)).isEqualTo(10L);
	}

	@Test
	void toSelectOptions3() throws Exception {
		SelectOptions options = Pageables.toSelectOptions(pageRequest(2, 5));
		assertThat(SelectOptionsAccessor.getOffset(options)).isEqualTo(10L);
		assertThat(SelectOptionsAccessor.getLimit(options)).isEqualTo(5L);
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
