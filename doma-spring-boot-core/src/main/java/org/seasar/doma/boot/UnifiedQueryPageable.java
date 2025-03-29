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
 * An adapter that integrates {@link Pageable} with Doma Criteria API.
 * <p>
 * This class allows converting {@link Pageable} pagination and sort information
 * into Doma Criteria API's limit, offset, and order-by specifications.
 * <p>
 * Example usage:
 * <pre>{@code
 * public Page<Task> getPage(Pageable pageable) {
 *     final var task_ = new Task_();
 *     final var p = UnifiedQueryPageable.from(pageable, task_);
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
 * }</pre>
 *
 * @author mazeneko
 */
public class UnifiedQueryPageable {
	private final Pageable pageable;
	private final Optional<SortConfig> sortConfig;

	/**
	 * A configuration holder for sort resolution.
	 */
	public record SortConfig(
			/** a resolver that maps property names to {@link PropertyMetamodel} */
			PropertyMetamodelResolver propertyMetamodelResolver,
			/** a consumer that applies default ordering when no valid sort can be determined */
			Consumer<OrderByNameDeclaration> defaultOrder) {
	}

	private UnifiedQueryPageable(
			Pageable pageable,
			Optional<SortConfig> sortConfig) {
		this.pageable = pageable;
		this.sortConfig = sortConfig;
	}

	/**
	 * Creates a {@link UnifiedQueryPageable}, resolving sort properties based on the entity's property names.
	 *
	 * @param pageable {@link Pageable} object to convert
	 * @param sortTargetEntity the target entity whose properties are used for sorting
	 * @return the {@link UnifiedQueryPageable}
	 */
	public static UnifiedQueryPageable from(
			Pageable pageable,
			EntityMetamodel<?> sortTargetEntity) {
		return UnifiedQueryPageable.from(pageable, sortTargetEntity, c -> {
		});
	}

	/**
	 * Creates a {@link UnifiedQueryPageable}, resolving sort properties based on the entity's property names.
	 *
	 * @param pageable {@link Pageable} object to convert
	 * @param sortTargetEntity the target entity whose properties are used for sorting
	 * @param defaultOrder a consumer that applies default ordering when no valid sort can be determined
	 * @return the {@link UnifiedQueryPageable}
	 */
	public static UnifiedQueryPageable from(
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
		return new UnifiedQueryPageable(
				pageable,
				Optional.of(sortConfig));
	}

	/**
	 * Creates a {@link UnifiedQueryPageable}
	 *
	 * @param pageable {@link Pageable} object to convert
	 * @param sortConfig sort configuration
	 * @return the {@link UnifiedQueryPageable}
	 */
	public static UnifiedQueryPageable of(Pageable pageable, SortConfig sortConfig) {
		return new UnifiedQueryPageable(pageable, Optional.of(sortConfig));
	}

	/**
	 * Creates a {@link UnifiedQueryPageable}
	 *
	 * @param pageable {@link Pageable} object to convert
	 * @param propertyMetamodelResolver a resolver that maps property names to {@link PropertyMetamodel}
	 * @return the {@link UnifiedQueryPageable}
	 */
	public static UnifiedQueryPageable of(
			Pageable pageable,
			PropertyMetamodelResolver propertyMetamodelResolver) {
		return UnifiedQueryPageable.of(pageable, propertyMetamodelResolver, c -> {
		});
	}

	/**
	 * Creates a {@link UnifiedQueryPageable}
	 *
	 * @param pageable {@link Pageable} object to convert
	 * @param propertyMetamodelResolver a resolver that maps property names to {@link PropertyMetamodel}
	 * @param defaultOrder a consumer that applies default ordering when no valid sort can be determined
	 * @return the {@link UnifiedQueryPageable}
	 */
	public static UnifiedQueryPageable of(
			Pageable pageable,
			PropertyMetamodelResolver propertyMetamodelResolver,
			Consumer<OrderByNameDeclaration> defaultOrder) {
		final var sortConfig = new SortConfig(
				propertyMetamodelResolver,
				defaultOrder);
		return new UnifiedQueryPageable(pageable, Optional.of(sortConfig));
	}

	/**
	 * Creates a {@link UnifiedQueryPageable} without sort configuration.
	 * Only limit and offset will be available.
	 *
	 * @param pageable {@link Pageable} object to convert
	 * @return the {@link UnifiedQueryPageable} without sort support
	 */
	public static UnifiedQueryPageable ofNonSort(Pageable pageable) {
		return new UnifiedQueryPageable(pageable, Optional.empty());
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
	 * @throws IllegalStateException if the sort configuration is missing
	 */
	public Consumer<OrderByNameDeclaration> orderBy() {
		final var sortConfig = this.sortConfig.orElseThrow(
				() -> new IllegalStateException("Sort configuration is required but not present."));
		if (pageable.getSort().isUnsorted()) {
			return sortConfig.defaultOrder();
		}
		final var orderSpecifiers = pageable
				.getSort()
				.flatMap(order -> sortConfig
						.propertyMetamodelResolver()
						.resolve(order.getProperty())
						.<Consumer<OrderByNameDeclaration>> map(
								propertyMetamodel -> switch (order.getDirection()) {
								case ASC -> c -> c.asc(propertyMetamodel);
								case DESC -> c -> c.desc(propertyMetamodel);
								})
						.stream())
				.toList();
		if (orderSpecifiers.isEmpty()) {
			return sortConfig.defaultOrder();
		}
		return c -> orderSpecifiers.forEach(orderSpecifier -> orderSpecifier.accept(c));
	}
}
