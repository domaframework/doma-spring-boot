package org.seasar.doma.boot;

import java.util.ArrayList;
import java.util.List;
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
 * An adapter that integrates {@link Pageable} with Doma Criteria API.
 * <p>
 * This class allows converting {@link Pageable} pagination and sort information
 * into Doma Criteria API's limit, offset, and order-by specifications.
 * <p>
 * Example usage:
 * <pre>{@code
 * public Page<Task> getPage(Pageable pageable) {
 *     final var task_ = new Task_();
 *     final var p = UnifiedCriteriaPageable.from(pageable, task_);
 *     final var content = this.queryDsl
 *         .from(task_)
 *         .offset(p.offset())
 *         .limit(p.limit())
 *         .orderBy(p.orderBy())
 *         .fetch();
 *     final var total = this.queryDsl
 *         .from(task_)
 *         .select(Expressions.count())
 *         .fetchOne();
 *     return new PageImpl<>(content, pageable, total);
 * }
 * }</pre>
 *
 * @author mazeneko
 */
public class UnifiedCriteriaPageable {
	private final Pageable pageable;
	private final SortConfig sortConfig;

	/**
	 * A configuration holder for sort resolution.
	 */
	public record SortConfig(
			/** a resolver that maps property names to {@link PropertyMetamodel} */
			PropertyMetamodelResolver propertyMetamodelResolver,
			/** a consumer that applies default ordering when no valid sort can be determined */
			Consumer<OrderByNameDeclaration> defaultOrder) {
	}

	private UnifiedCriteriaPageable(
			Pageable pageable,
			SortConfig sortConfig) {
		this.pageable = pageable;
		this.sortConfig = sortConfig;
	}

	public Pageable getPageable() {
		return pageable;
	}

	public SortConfig getSortConfig() {
		return sortConfig;
	}

	/**
	 * Creates a {@link UnifiedCriteriaPageable}, resolving sort properties based on the entity's property names.
	 *
	 * @param pageable {@link Pageable} object to convert
	 * @param sortTargetEntity the target entity whose properties are used for sorting
	 * @return the {@link UnifiedCriteriaPageable}
	 */
	public static UnifiedCriteriaPageable from(
			Pageable pageable,
			EntityMetamodel<?> sortTargetEntity) {
		return UnifiedCriteriaPageable.from(pageable, sortTargetEntity, c -> {
		});
	}

	/**
	 * Creates a {@link UnifiedCriteriaPageable}, resolving sort properties based on the entity's property names.
	 *
	 * @param pageable {@link Pageable} object to convert
	 * @param sortTargetEntity the target entity whose properties are used for sorting
	 * @param defaultOrder a consumer that applies default ordering when no valid sort can be determined
	 * @return the {@link UnifiedCriteriaPageable}
	 */
	public static UnifiedCriteriaPageable from(
			Pageable pageable,
			EntityMetamodel<?> sortTargetEntity,
			Consumer<OrderByNameDeclaration> defaultOrder) {
		final var nameToMetamodel = sortTargetEntity
				.allPropertyMetamodels()
				.stream()
				.collect(Collectors.toMap(PropertyMetamodel::getName, Function.identity()));
		final var sortConfig = new SortConfig(
				propertyName -> Optional.ofNullable(nameToMetamodel.get(propertyName)),
				defaultOrder);
		return new UnifiedCriteriaPageable(
				pageable,
				sortConfig);
	}

	/**
	 * Creates a {@link UnifiedCriteriaPageable}
	 *
	 * @param pageable {@link Pageable} object to convert
	 * @param sortConfig sort configuration
	 * @return the {@link UnifiedCriteriaPageable}
	 */
	public static UnifiedCriteriaPageable of(Pageable pageable, SortConfig sortConfig) {
		return new UnifiedCriteriaPageable(pageable, sortConfig);
	}

	/**
	 * Creates a {@link UnifiedCriteriaPageable}
	 *
	 * @param pageable {@link Pageable} object to convert
	 * @param propertyMetamodelResolver a resolver that maps property names to {@link PropertyMetamodel}
	 * @return the {@link UnifiedCriteriaPageable}
	 */
	public static UnifiedCriteriaPageable of(
			Pageable pageable,
			PropertyMetamodelResolver propertyMetamodelResolver) {
		return UnifiedCriteriaPageable.of(pageable, propertyMetamodelResolver, c -> {
		});
	}

	/**
	 * Creates a {@link UnifiedCriteriaPageable}
	 *
	 * @param pageable {@link Pageable} object to convert
	 * @param propertyMetamodelResolver a resolver that maps property names to {@link PropertyMetamodel}
	 * @param defaultOrder a consumer that applies default ordering when no valid sort can be determined
	 * @return the {@link UnifiedCriteriaPageable}
	 */
	public static UnifiedCriteriaPageable of(
			Pageable pageable,
			PropertyMetamodelResolver propertyMetamodelResolver,
			Consumer<OrderByNameDeclaration> defaultOrder) {
		final var sortConfig = new SortConfig(
				propertyMetamodelResolver,
				defaultOrder);
		return new UnifiedCriteriaPageable(pageable, sortConfig);
	}

	/**
	 * Converts {@link Pageable} to {@link EntityQueryable#limit(Integer)}
	 *
	 * @return the limit.
	 *         if {@link Pageable#isUnpaged()} is {@code true} then null.
	 */
	public Integer limit() {
		return pageable.isUnpaged() ? null : pageable.getPageSize();
	}

	/**
	 * Converts {@link Pageable} to {@link EntityQueryable#offset(Integer)}
	 *
	 * @return the offset.
	 *         if {@link Pageable#isUnpaged()} is {@code true} then null.
	 */
	public Integer offset() {
		return pageable.isUnpaged()
				? null
				: Math.multiplyExact(pageable.getPageNumber(), pageable.getPageSize());
	}

	/**
	 * Creates an {@link OrderByNameDeclaration} consumer based on the
	 * {@link Pageable}'s sort information using the provided {@link PropertyMetamodelResolver}.
	 * <p>
	 * If the {@link Pageable} is unsorted or no matching {@link PropertyMetamodel} is found,
	 * a default ordering is applied.
	 *
	 * @return a consumer that configures ordering based on the resolved {@link PropertyMetamodel} instances
	 */
	public Consumer<OrderByNameDeclaration> orderBy() {
		return orderBy(missingProperties -> {
		});
	}

	/**
	 * Creates an {@link OrderByNameDeclaration} consumer based on the
	 * {@link Pageable}'s sort information using the provided {@link PropertyMetamodelResolver}.
	 * <p>
	 * If the {@link Pageable} is unsorted, or if no {@link PropertyMetamodel} can be resolved
	 * for a given sort property, a default ordering is applied.
	 * <p>
	 * The provided {@code handleMissingProperties} consumer is called with a list of
	 * property names that could not be resolved. This can be used to throw an exception,
	 * log a warning, or handle the situation in a custom way.
	 *
	 * @param handleMissingProperties a callback that handles property names which could not be resolved
	 * @return a consumer that configures ordering based on the resolved {@link PropertyMetamodel} instances
	 */
	public Consumer<OrderByNameDeclaration> orderBy(
			Consumer<List<String>> handleMissingProperties) {
		if (pageable.getSort().isUnsorted()) {
			return sortConfig.defaultOrder();
		}
		final var orderSpecifiers = new ArrayList<Consumer<OrderByNameDeclaration>>();
		final var missingProperties = new ArrayList<String>();
		for (final var order : pageable.getSort()) {
			sortConfig
					.propertyMetamodelResolver()
					.resolve(order.getProperty())
					.ifPresentOrElse(
							propertyMetamodel -> orderSpecifiers.add(
									switch (order.getDirection()) {
									case ASC -> c -> c.asc(propertyMetamodel);
									case DESC -> c -> c.desc(propertyMetamodel);
									}),
							() -> missingProperties.add(order.getProperty()));
		}
		if (!missingProperties.isEmpty()) {
			handleMissingProperties.accept(missingProperties);
		}
		if (orderSpecifiers.isEmpty()) {
			return sortConfig.defaultOrder();
		}
		return c -> orderSpecifiers.forEach(orderSpecifier -> orderSpecifier.accept(c));
	}
}
