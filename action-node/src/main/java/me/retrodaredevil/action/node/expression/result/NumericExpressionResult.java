package me.retrodaredevil.action.node.expression.result;

import org.jetbrains.annotations.NotNull;

public interface NumericExpressionResult extends ExpressionResult, Comparable<NumericExpressionResult> {
	Number getNumber();

	static NumericExpressionResult create(Number number) {
		return () -> number;
	}

	@Override
	default int compareTo(@NotNull NumericExpressionResult o) {
		return Double.compare(getNumber().doubleValue(), o.getNumber().doubleValue());
	}
}
