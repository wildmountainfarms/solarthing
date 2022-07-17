package me.retrodaredevil.action.node.expression;

import me.retrodaredevil.action.node.expression.result.ExpressionResult;

import java.util.List;

public interface Expression {
	List<? extends ExpressionResult> evaluate();
}
