package me.retrodaredevil.action.node.expression.result;

import static java.util.Objects.requireNonNull;

public final class StringExpressionResult implements ExpressionResult {
	private final String value;

	public StringExpressionResult(String value) {
		this.value = requireNonNull(value);
	}

	public String getString() {
		return value;
	}
}
