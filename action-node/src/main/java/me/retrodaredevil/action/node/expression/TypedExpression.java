package me.retrodaredevil.action.node.expression;

import me.retrodaredevil.action.node.expression.result.ExpressionResult;
import me.retrodaredevil.action.node.expression.type.ExpressionType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public interface TypedExpression<T extends ExpressionResult> extends Expression {

	@Override
	List<? extends T> evaluate();

	// all implementations of TypedExpression should return non-null type (superinterface allows Nullable)
	@Override
	ExpressionType getType();
}
