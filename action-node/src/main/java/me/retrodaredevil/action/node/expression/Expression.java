package me.retrodaredevil.action.node.expression;

import me.retrodaredevil.action.node.expression.result.ExpressionResult;
import me.retrodaredevil.action.node.expression.type.ExpressionType;

import java.util.List;

import static java.util.Objects.requireNonNull;

public interface Expression {
	List<? extends ExpressionResult> evaluate();

	/**
	 * @return The known type of the expression or null. Null represents that the type is unknown until {@link #evaluate()} is called
	 */
	default ExpressionType getType() {
		return null;
	}
	default boolean doesNotSupport(ExpressionType expressionType) {
		requireNonNull(expressionType);
		ExpressionType type = getType();
		if (type == null) {
			return false; // we might support this
		}
		return type != expressionType;
	}
	default void checkSupport(ExpressionType expressionType) {
		if (doesNotSupport(expressionType)) {
			throw new IllegalArgumentException("expression: " + this + " does not support the " + expressionType + " type!");
		}
	}
}
