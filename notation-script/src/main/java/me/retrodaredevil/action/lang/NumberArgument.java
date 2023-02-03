package me.retrodaredevil.action.lang;

import static java.util.Objects.requireNonNull;

public final class NumberArgument implements Argument {
	private final Number value;

	public NumberArgument(Number value) {
		this.value = requireNonNull(value);
	}

	public Number getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
