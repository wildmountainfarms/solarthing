package me.retrodaredevil.action.node.expression.result;

public interface BooleanExpressionResult extends ExpressionResult {
	boolean getBoolean();

	static BooleanExpressionResult create(boolean result) {
		return () -> result;
	}
}
