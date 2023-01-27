package me.retrodaredevil.action.lang;

import static java.util.Objects.requireNonNull;

public final class StringArgument implements Argument {
	private final String value;

	public StringArgument(String value) {
		this.value = requireNonNull(value);
	}

	public String getValue() {
		return value;
	}
}
