package me.retrodaredevil.action.lang;

import com.fasterxml.jackson.databind.node.TextNode;

import static java.util.Objects.requireNonNull;

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
		return new TextNode(value).toString();
	}
}
