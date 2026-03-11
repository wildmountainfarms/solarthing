package me.retrodaredevil.notation;

import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@NullMarked
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
