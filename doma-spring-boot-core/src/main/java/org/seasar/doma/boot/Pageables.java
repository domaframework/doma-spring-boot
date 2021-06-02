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
