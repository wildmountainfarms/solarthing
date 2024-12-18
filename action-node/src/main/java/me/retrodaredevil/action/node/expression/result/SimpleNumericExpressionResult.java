package me.retrodaredevil.action.node.expression.result;

import static java.util.Objects.requireNonNull;

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
	public boolean equals(Object o) {
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
