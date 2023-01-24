package me.retrodaredevil.action.node.expression;

import me.retrodaredevil.action.node.expression.result.BooleanExpressionResult;
import me.retrodaredevil.action.node.expression.type.ExpressionType;
import me.retrodaredevil.action.node.expression.type.PrimitiveExpressionType;

import java.util.List;

public interface BooleanExpression extends TypedExpression<BooleanExpressionResult> {
	static BooleanExpression createConstant(List<? extends BooleanExpressionResult> values) {
		return () -> values;
	}

	@Override
	default ExpressionType getType() {
		return PrimitiveExpressionType.BOOLEAN;
	}
}
