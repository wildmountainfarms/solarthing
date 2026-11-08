package me.retrodaredevil.notation;

import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.node.StringNode;

import static java.util.Objects.requireNonNull;

@NullMarked
public final class StringArgument implements Argument {
	private final String value;

	public StringArgument(String value) {
		this.value = requireNonNull(value);
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return new StringNode(value).toString();
	}
}
