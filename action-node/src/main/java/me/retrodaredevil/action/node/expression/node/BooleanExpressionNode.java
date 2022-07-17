package me.retrodaredevil.action.node.expression.node;


import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.BooleanExpression;

public interface BooleanExpressionNode extends ExpressionNode {
	@Override
	BooleanExpression createExpression(ActionEnvironment actionEnvironment);
}
