package me.retrodaredevil.action.node.expression.result;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

@NullMarked
public final class StringExpressionResult implements ExpressionResult {
	private final String value;

	public StringExpressionResult(String value) {
		this.value = requireNonNull(value);
	}

	public String getString() {
		return value;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof StringExpressionResult)) return false;
		StringExpressionResult that = (StringExpressionResult) o;
		return value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}
}
