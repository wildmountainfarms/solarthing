package me.retrodaredevil.action.node.expression.result;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@NullMarked
final class SimpleNumericExpressionResult implements NumericExpressionResult {
	private final Number number;

	SimpleNumericExpressionResult(Number number) {
		this.number = requireNonNull(number);
	}

	@Override
	public Number getNumber() {
		return number;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof NumericExpressionResult)) return false;
		NumericExpressionResult that = (NumericExpressionResult) o;
		Number thatNumber = that.getNumber();
		return number.equals(thatNumber) || number.doubleValue() == thatNumber.doubleValue();
	}

	@Override
	public int hashCode() {
		return Double.hashCode(number.doubleValue());
	}
}
