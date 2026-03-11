package me.retrodaredevil.action.node.expression.result;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface ExpressionResult {
	@Override
	boolean equals(@Nullable Object other);
}
