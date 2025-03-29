package org.seasar.doma.boot;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.seasar.doma.jdbc.criteria.declaration.OrderByNameDeclaration;
import org.seasar.doma.jdbc.criteria.metamodel.EntityMetamodel;
import org.seasar.doma.jdbc.criteria.metamodel.PropertyMetamodel;
import org.seasar.doma.jdbc.criteria.statement.EntityQueryable;
import org.springframework.data.domain.Pageable;

/**
 * Converts Utilities for {@link Pageable} to be used with Doma Criteria.
 *
 * @author mazeneko
 */
public class UnifiedQueryPageable {
	/**
	 * Converts {@link Pageable} to {@link EntityQueryable#limit(Integer)}
	 *
	 * @param pageable {@link Pageable} object to convert
	 * @return the limit.
	 *         if {@link Pageable#isUnpaged()} is {@code true} then null.
	 */
	public static Integer limit(Pageable pageable) {
		return pageable.isUnpaged() ? null : pageable.getPageSize();
	}

	/**
	 * Converts {@link Pageable} to {@link EntityQueryable#offset(Integer)}
	 *
	 * @param pageable {@link Pageable} object to convert
	 * @return the offset.
	 *         if {@link Pageable#isUnpaged()} is {@code true} then null.
	 */
	public static Integer offset(Pageable pageable) {
		return pageable.isUnpaged() ? null
				: Math.multiplyExact(pageable.getPageNumber(), pageable.getPageSize());
	}

	/**
	 * Creates an {@link OrderByNameDeclaration} consumer for a single entity based
	 * on the {@link Pageable}'s sort information.
	 * <p>
	 * This method resolves property names for ordering within the specified entity
	 * using its metamodel, and generates
	 * a consumer that can be used to apply ascending/descending orders.
	 * <p>
	 * If the {@link Pageable} is unsorted, no ordering is applied.
	 *
	 * @param pageable        the {@link Pageable} containing sorting information
	 * @param entityMetamodel the {@link EntityMetamodel} corresponding to the
	 *                        target entity
	 * @return a consumer that configures ordering on the target entity
	 */
	public static Consumer<OrderByNameDeclaration> orderBySingleEntity(
			Pageable pageable,
			EntityMetamodel<?> entityMetamodel) {
		return orderBySingleEntity(pageable, entityMetamodel, c -> {
		});
	}

	/**
	 * Creates an {@link OrderByNameDeclaration} consumer for a single entity based
	 * on the {@link Pageable}'s sort information.
	 * <p>
	 * This method resolves property names for ordering within the specified entity
	 * using its metamodel, and generates
	 * a consumer that can be used to apply ascending/descending orders.
	 * <p>
	 * a default ordering via {@code defaultOrder} if the given {@link Pageable} is
	 * unsorted.
	 *
	 * @param pageable        the {@link Pageable} containing sorting information
	 * @param entityMetamodel the {@link EntityMetamodel} corresponding to the
	 *                        target entity
	 * @param defaultOrder    a consumer that applies default ordering if the
	 *                        {@link Pageable} is unsorted
	 * @return a consumer that configures ordering on the target entity
	 */
	public static Consumer<OrderByNameDeclaration> orderBySingleEntity(
			Pageable pageable,
			EntityMetamodel<?> entityMetamodel,
			Consumer<OrderByNameDeclaration> defaultOrder) {
		final var nameToMetamodel = entityMetamodel
				.allPropertyMetamodels()
				.stream()
				.collect(Collectors.toMap(PropertyMetamodel::getName, Function.identity()));
		return orderBy(
				pageable,
				propertyName -> Optional.ofNullable(nameToMetamodel.get(propertyName)),
				defaultOrder);
	}

	/**
	 * Creates an {@link OrderByNameDeclaration} consumer based on the
	 * {@link Pageable}'s sort information
	 * using the provided {@link PropertyMetamodelResolver}.
	 * <p>
	 * If the {@link Pageable} is unsorted, no ordering is applied.
	 *
	 * @param pageable                  the {@link Pageable} containing sorting
	 *                                  information
	 * @param propertyMetamodelResolver a resolver that maps property names to
	 *                                  {@link PropertyMetamodel}
	 * @return a consumer that configures ordering based on the resolved
	 *         {@link PropertyMetamodel} instances
	 */
	public static Consumer<OrderByNameDeclaration> orderBy(
			Pageable pageable,
			PropertyMetamodelResolver propertyMetamodelResolver) {
		return orderBy(pageable, propertyMetamodelResolver, c -> {
		});
	}

	/**
	 * Creates an {@link OrderByNameDeclaration} consumer based on the
	 * {@link Pageable}'s sort information
	 * using the provided {@link PropertyMetamodelResolver}.
	 * <p>
	 * a default ordering via {@code defaultOrder} if the given {@link Pageable} is
	 * unsorted.
	 *
	 * @param pageable                  the {@link Pageable} containing sorting
	 *                                  information
	 * @param propertyMetamodelResolver a resolver that maps property names to
	 *                                  {@link PropertyMetamodel}
	 * @param defaultOrder              a consumer that applies default ordering if
	 *                                  the {@link Pageable} is unsorted
	 * @return a consumer that configures ordering based on the resolved
	 *         {@link PropertyMetamodel} instances
	 */
	public static Consumer<OrderByNameDeclaration> orderBy(
			Pageable pageable,
			PropertyMetamodelResolver propertyMetamodelResolver,
			Consumer<OrderByNameDeclaration> defaultOrder) {
		if (pageable.getSort().isUnsorted()) {
			return defaultOrder;
		}
		final var orderSpecifiers = pageable
				.getSort()
				.flatMap(order -> propertyMetamodelResolver
						.resolve(order.getProperty())
						.<Consumer<OrderByNameDeclaration>> map(
								propertyMetamodel -> switch (order.getDirection()) {
								case ASC -> c -> c.asc(propertyMetamodel);
								case DESC -> c -> c.desc(propertyMetamodel);
								})
						.stream())
				.toList();
		if (orderSpecifiers.isEmpty()) {
			return defaultOrder;
		}
		return c -> orderSpecifiers.forEach(orderSpecifier -> orderSpecifier.accept(c));
	}
}
