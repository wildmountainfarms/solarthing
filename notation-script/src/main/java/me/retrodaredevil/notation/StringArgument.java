package me.retrodaredevil.notation;

import com.fasterxml.jackson.databind.node.TextNode;
import org.jspecify.annotations.NullMarked;

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
		return new TextNode(value).toString();
	}
}
