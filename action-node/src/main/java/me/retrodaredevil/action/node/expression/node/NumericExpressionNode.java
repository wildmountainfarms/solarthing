package me.retrodaredevil.action.node.expression.node;

import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.NumericExpression;

public interface NumericExpressionNode extends ExpressionNode {
	@Override
	NumericExpression createExpression(ActionEnvironment actionEnvironment);
}
