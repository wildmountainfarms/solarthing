package me.retrodaredevil.action.node.expression;

import me.retrodaredevil.action.node.expression.result.ExpressionResult;

import java.util.List;

public interface TypedExpression<T extends ExpressionResult> extends Expression {

	@Override
	List<? extends T> evaluate();
}
