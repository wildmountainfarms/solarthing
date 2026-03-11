package me.retrodaredevil.action.node.expression;

import me.retrodaredevil.action.node.expression.result.StringExpressionResult;
import me.retrodaredevil.action.node.expression.type.ExpressionType;
import me.retrodaredevil.action.node.expression.type.PrimitiveExpressionType;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface StringExpression extends TypedExpression<StringExpressionResult> {
	static StringExpression createConstant(List<? extends StringExpressionResult> values) {
		return () -> values;
	}

	@Override
	default ExpressionType getType() {
		return PrimitiveExpressionType.STRING;
	}
}
