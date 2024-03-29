package me.retrodaredevil.action.node.expression;

import me.retrodaredevil.action.node.expression.result.NumericExpressionResult;
import me.retrodaredevil.action.node.expression.type.ExpressionType;
import me.retrodaredevil.action.node.expression.type.PrimitiveExpressionType;

import java.util.List;

public interface NumericExpression extends TypedExpression<NumericExpressionResult> {

	static NumericExpression createConstant(List<? extends NumericExpressionResult> values) {
		return () -> values;
	}

	@Override
	default ExpressionType getType() {
		return PrimitiveExpressionType.NUMERIC;
	}
}
