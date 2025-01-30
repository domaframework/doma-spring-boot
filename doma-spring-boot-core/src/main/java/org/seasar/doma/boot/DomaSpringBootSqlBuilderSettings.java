package org.seasar.doma.boot;

import java.util.function.Predicate;

import org.seasar.doma.jdbc.SqlBuilderSettings;

public class DomaSpringBootSqlBuilderSettings implements SqlBuilderSettings {

	private final Predicate<String> shouldRemoveBlockComment;
	private final Predicate<String> shouldRemoveLineComment;
	private final boolean shouldRemoveBlankLines;
	private final boolean shouldRequireInListPadding;

	public DomaSpringBootSqlBuilderSettings(Predicate<String> shouldRemoveBlockComment,
			Predicate<String> shouldRemoveLineComment, boolean shouldRemoveBlankLines,
			boolean shouldRequireInListPadding) {
		this.shouldRemoveBlockComment = shouldRemoveBlockComment;
		this.shouldRemoveLineComment = shouldRemoveLineComment;
		this.shouldRemoveBlankLines = shouldRemoveBlankLines;
		this.shouldRequireInListPadding = shouldRequireInListPadding;
	}

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
