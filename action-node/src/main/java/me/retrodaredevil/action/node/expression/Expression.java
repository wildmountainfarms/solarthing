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
		// TODO determine if this method is even needed. (When is it actually needed by something and when does it get in the way)
		//   As of 2023.02.02 we don't even implement it everywhere since null is a valid value in any scenario with the recent introduction of the ref implementation
		return null;
	}

	default Expression evaluateToConstant() {
		List<? extends ExpressionResult> resultList = evaluate();
		ExpressionType expressionType = getType();
		return new Expression() {
			@Override
			public List<? extends ExpressionResult> evaluate() {
				return resultList;
			}

			@Override
			public ExpressionType getType() {
				return expressionType;
			}
		};
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
