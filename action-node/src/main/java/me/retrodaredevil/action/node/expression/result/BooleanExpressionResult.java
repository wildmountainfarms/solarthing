package me.retrodaredevil.action.node.expression.result;

public enum BooleanExpressionResult implements ExpressionResult {
	TRUE(true),
	FALSE(false),
	;
	private final boolean value;

	BooleanExpressionResult(boolean value) {
		this.value = value;
	}

	public boolean getBoolean() {
		return value;
	}

	public BooleanExpressionResult not() {
		return this == TRUE ? FALSE : TRUE;
	}

	public static BooleanExpressionResult get(boolean result) {
		return result ? TRUE : FALSE;
	}
}
