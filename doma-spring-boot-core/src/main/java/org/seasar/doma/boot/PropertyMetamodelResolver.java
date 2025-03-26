package org.seasar.doma.boot;

import java.util.Optional;

import org.seasar.doma.jdbc.criteria.metamodel.PropertyMetamodel;

/**
 * A resolver that maps property names to {@link PropertyMetamodel}
 */
@FunctionalInterface
public interface PropertyMetamodelResolver {
	/**
	 * Resolves the specified property name into a {@link PropertyMetamodel}.
	 *
	 * @param propertyName the name of the property to resolve
	 * @return an {@link Optional} containing the resolved {@link PropertyMetamodel}
	 *         if found,
	 *         or an empty {@link Optional} if the property name cannot be resolved
	 */
	Optional<PropertyMetamodel<?>> resolve(String propertyName);
}
