package org.seasar.doma.boot;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.seasar.doma.jdbc.criteria.declaration.OrderByNameDeclaration;
import org.seasar.doma.jdbc.criteria.metamodel.EntityMetamodel;
import org.seasar.doma.jdbc.criteria.metamodel.PropertyMetamodel;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class UnifiedQueryPageableTest {
	@ParameterizedTest
	@CsvSource(value = {
			"0 | 10 | 0 | 10",
			"2 | 10 | 20 | 10",
			"2 | 5 | 10 | 5",
	}, delimiter = '|')
	public void testOffsetAndLimit(
			int pageNumber, int pageSize, int expectedOffset, int expectedLimit) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		UnifiedQueryPageable p = UnifiedQueryPageable.of(pageable, c -> Optional.empty());

		Integer offset = p.offset();
		Integer limit = p.limit();

		assertThat(offset, is(expectedOffset));
		assertThat(limit, is(expectedLimit));
	}

	@Test
	public void testOffsetAndLimitWhenUnpaged() {
		Pageable pageable = Pageable.unpaged();
		UnifiedQueryPageable p = UnifiedQueryPageable.of(pageable, c -> Optional.empty());

		Integer offset = p.offset();
		Integer limit = p.limit();

		assertThat(offset, nullValue());
		assertThat(limit, nullValue());
	}

	@Test
	public void testOrderBy() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
		PropertyMetamodel<?> nameProp = mock(PropertyMetamodel.class);
		UnifiedQueryPageable p = UnifiedQueryPageable.of(
				pageable,
				propertyName -> switch (propertyName) {
				case "name" -> Optional.of(nameProp);
				default -> Optional.empty();
				});

		Consumer<OrderByNameDeclaration> consumer = p.orderBy();

		OrderByNameDeclaration orderByNameDeclaration = mock(OrderByNameDeclaration.class);
		consumer.accept(orderByNameDeclaration);
		verify(orderByNameDeclaration, times(1)).asc(nameProp);
	}

	@Test
	public void testOrderBy2() {
		Pageable pageable = PageRequest.of(0, 10,
				Sort.by("name").descending().and(Sort.by("age").ascending()));
		PropertyMetamodel<?> nameProp = mock(PropertyMetamodel.class);
		PropertyMetamodel<?> ageProp = mock(PropertyMetamodel.class);
		UnifiedQueryPageable p = UnifiedQueryPageable.of(
				pageable,
				propertyName -> switch (propertyName) {
				case "name" -> Optional.of(nameProp);
				case "age" -> Optional.of(ageProp);
				default -> Optional.empty();
				});

		Consumer<OrderByNameDeclaration> consumer = p.orderBy();

		OrderByNameDeclaration orderByNameDeclaration = mock(OrderByNameDeclaration.class);
		consumer.accept(orderByNameDeclaration);
		verify(orderByNameDeclaration, times(1)).desc(nameProp);
		verify(orderByNameDeclaration, times(1)).asc(ageProp);
	}

	@Test
	public void testOrderByWhenNonSort() {
		Pageable pageable = PageRequest.of(0, 10);
		UnifiedQueryPageable p = UnifiedQueryPageable.of(
				pageable,
				propertyName -> Optional.empty());

		Consumer<OrderByNameDeclaration> consumer = p.orderBy();

		OrderByNameDeclaration orderByNameDeclaration = mock(OrderByNameDeclaration.class);
		consumer.accept(orderByNameDeclaration);
		verifyNoMoreInteractions(orderByNameDeclaration);
	}

	@Test
	public void testOrderByWhenNonSortAndSetDefault() {
		Pageable pageable = PageRequest.of(0, 10);
		PropertyMetamodel<?> idProp = mock(PropertyMetamodel.class);
		UnifiedQueryPageable p = UnifiedQueryPageable.of(
				pageable,
				propertyName -> Optional.empty(),
				t -> t.asc(idProp));

		Consumer<OrderByNameDeclaration> consumer = p.orderBy();

		OrderByNameDeclaration orderByNameDeclaration = mock(OrderByNameDeclaration.class);
		consumer.accept(orderByNameDeclaration);
		verify(orderByNameDeclaration, times(1)).asc(idProp);
	}

	@Test
	public void testOrderBySingleEntity() {
		Pageable pageable = PageRequest.of(0, 10,
				Sort.by("name").descending().and(Sort.by("age").ascending()));
		PropertyMetamodel<?> nameProp = mock(PropertyMetamodel.class);
		when(nameProp.getName()).thenReturn("name");
		PropertyMetamodel<?> ageProp = mock(PropertyMetamodel.class);
		when(ageProp.getName()).thenReturn("age");
		EntityMetamodel<?> entity = mock(EntityMetamodel.class);
		when(entity.allPropertyMetamodels()).thenReturn(List.of(nameProp, ageProp));
		UnifiedQueryPageable p = UnifiedQueryPageable.from(pageable, entity);

		Consumer<OrderByNameDeclaration> consumer = p.orderBy();

		OrderByNameDeclaration orderByNameDeclaration = mock(OrderByNameDeclaration.class);
		consumer.accept(orderByNameDeclaration);
		verify(orderByNameDeclaration, times(1)).desc(nameProp);
		verify(orderByNameDeclaration, times(1)).asc(ageProp);
	}
}
