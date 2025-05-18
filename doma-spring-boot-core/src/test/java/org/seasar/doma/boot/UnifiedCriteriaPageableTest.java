package org.seasar.doma.boot;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.jdbc.criteria.declaration.OrderByNameDeclaration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class UnifiedCriteriaPageableTest {
	@ParameterizedTest
	@CsvSource(value = {
			"0 | 10 | 0 | 10",
			"2 | 10 | 20 | 10",
			"2 | 5 | 10 | 5",
	}, delimiter = '|')
	public void testOffsetAndLimit(
			int pageNumber, int pageSize, int expectedOffset, int expectedLimit) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		UnifiedCriteriaPageable p = UnifiedCriteriaPageable.of(pageable, c -> Optional.empty());

		Integer offset = p.offset();
		Integer limit = p.limit();

		assertEquals(expectedOffset, offset);
		assertEquals(expectedLimit, limit);
	}

	@Test
	public void testOffsetAndLimitWhenUnpaged() {
		Pageable pageable = Pageable.unpaged();
		UnifiedCriteriaPageable p = UnifiedCriteriaPageable.of(pageable, c -> Optional.empty());

		Integer offset = p.offset();
		Integer limit = p.limit();

		assertNull(offset);
		assertNull(limit);
	}

	@Test
	public void testOrderBy() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
		Person_ entity = new Person_();
		UnifiedCriteriaPageable p = UnifiedCriteriaPageable.of(
				pageable,
				propertyName -> switch (propertyName) {
				case "name" -> Optional.of(entity.name);
				default -> Optional.empty();
				});

		Consumer<OrderByNameDeclaration> consumer = p.orderBy();

		OrderByNameDeclaration orderByNameDeclaration = mock(OrderByNameDeclaration.class);
		consumer.accept(orderByNameDeclaration);
		verify(orderByNameDeclaration, times(1)).asc(entity.name);
	}

	@Test
	public void testOrderBy2() {
		Pageable pageable = PageRequest.of(0, 10,
				Sort.by("name").descending().and(Sort.by("age").ascending()));
		Person_ entity = new Person_();
		UnifiedCriteriaPageable p = UnifiedCriteriaPageable.of(
				pageable,
				propertyName -> switch (propertyName) {
				case "name" -> Optional.of(entity.name);
				case "age" -> Optional.of(entity.age);
				default -> Optional.empty();
				});

		Consumer<OrderByNameDeclaration> consumer = p.orderBy();

		OrderByNameDeclaration orderByNameDeclaration = mock(OrderByNameDeclaration.class);
		consumer.accept(orderByNameDeclaration);
		var sortOrderVerifier = inOrder(orderByNameDeclaration);
		sortOrderVerifier.verify(orderByNameDeclaration, times(1)).desc(entity.name);
		sortOrderVerifier.verify(orderByNameDeclaration, times(1)).asc(entity.age);
	}

	@Test
	public void testOrderByWhenNonSort() {
		Pageable pageable = PageRequest.of(0, 10);
		UnifiedCriteriaPageable p = UnifiedCriteriaPageable.of(
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
		Person_ entity = new Person_();
		Consumer<OrderByNameDeclaration> defaultOrder = c -> c.asc(entity.id);
		UnifiedCriteriaPageable p = UnifiedCriteriaPageable.of(
				pageable,
				propertyName -> Optional.empty(),
				defaultOrder);

		Consumer<OrderByNameDeclaration> consumer = p.orderBy();

		assertSame(defaultOrder, consumer);
	}

	@Test
	public void testOrderBySingleEntity() {
		Pageable pageable = PageRequest.of(0, 10,
				Sort.by("name").descending().and(Sort.by("age").ascending()));
		Person_ entity = new Person_();
		UnifiedCriteriaPageable p = UnifiedCriteriaPageable.from(pageable, entity);

		Consumer<OrderByNameDeclaration> consumer = p.orderBy();

		OrderByNameDeclaration orderByNameDeclaration = mock(OrderByNameDeclaration.class);
		consumer.accept(orderByNameDeclaration);
		var sortOrderVerifier = inOrder(orderByNameDeclaration);
		sortOrderVerifier.verify(orderByNameDeclaration, times(1)).desc(entity.name);
		sortOrderVerifier.verify(orderByNameDeclaration, times(1)).asc(entity.age);
	}

	@Test
	public void testOrderByWhenMissingProperties() {
		Pageable pageable = PageRequest.of(0, 10,
				Sort.by("dog").and(Sort.by("name")).and(Sort.by("cat")));
		Person_ entity = new Person_();
		UnifiedCriteriaPageable p = UnifiedCriteriaPageable.from(pageable, entity);

		Consumer<OrderByNameDeclaration> consumer = p.orderBy();

		OrderByNameDeclaration orderByNameDeclaration = mock(OrderByNameDeclaration.class);
		consumer.accept(orderByNameDeclaration);
		verify(orderByNameDeclaration, times(1)).asc(entity.name);
	}

	@Test
	public void testOrderByWhenMissingAllProperties() {
		Pageable pageable = PageRequest.of(0, 10,
				Sort.by("dog").and(Sort.by("cat")));
		Person_ entity = new Person_();
		Consumer<OrderByNameDeclaration> defaultOrder = c -> c.desc(entity.age);
		UnifiedCriteriaPageable p = UnifiedCriteriaPageable.from(pageable, entity, defaultOrder);

		Consumer<OrderByNameDeclaration> consumer = p.orderBy();

		assertSame(defaultOrder, consumer);
	}

	@Test
	public void testOrderByWhenMissingPropertiesHandle() {
		Pageable pageable = PageRequest.of(0, 10,
				Sort.by("dog").and(Sort.by("name")).and(Sort.by("cat")));
		Person_ entity = new Person_();
		UnifiedCriteriaPageable p = UnifiedCriteriaPageable.from(pageable, entity);

		assertThatThrownBy(() -> p.orderBy(missingProperties -> {
			throw new IllegalArgumentException(
					missingProperties.stream().collect(Collectors.joining(",")));
		}))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("dog,cat");
	}
}

@Entity(metamodel = @Metamodel)
record Person(@Id String id, String name, Integer age) {
}
