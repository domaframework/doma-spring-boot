package org.seasar.doma.boot;

import java.util.function.Predicate;

import org.seasar.doma.jdbc.SqlBuilderSettings;

/**
 * Implementation of {@link SqlBuilderSettings} for Spring Boot.
 * <p>
 * This record encapsulates the SQL builder settings used by Doma in a Spring Boot application.
 */
public record DomaSpringBootSqlBuilderSettings(
        Predicate<String> shouldRemoveBlockComment,
        Predicate<String> shouldRemoveLineComment,
        boolean shouldRemoveBlankLines,
        boolean shouldRequireInListPadding) implements SqlBuilderSettings {

    @Override
    public boolean shouldRemoveBlockComment(String comment) {
        return shouldRemoveBlockComment.test(comment);
    }

    @Override
    public boolean shouldRemoveLineComment(String comment) {
        return shouldRemoveLineComment.test(comment);
    }

    @Override
    public boolean shouldRemoveBlankLines() {
        return shouldRemoveBlankLines;
    }

    @Override
    public boolean shouldRequireInListPadding() {
        return shouldRequireInListPadding;
    }
}
