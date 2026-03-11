package me.retrodaredevil.action.node.expression.result;

import org.jspecify.annotations.NonNull;

/**
 * A {@link NumericExpressionResult} contains a {@link Number} of any implementation.
 * Equality is determined by comparing the equality of the {@link Number#doubleValue()}
 */
public interface NumericExpressionResult extends ExpressionResult, Comparable<NumericExpressionResult> {
	Number getNumber();

	static NumericExpressionResult create(Number number) {
		return new SimpleNumericExpressionResult(number);
	}

	@Override
	default int compareTo(@NonNull NumericExpressionResult o) {
		return Double.compare(getNumber().doubleValue(), o.getNumber().doubleValue());
	}
}
