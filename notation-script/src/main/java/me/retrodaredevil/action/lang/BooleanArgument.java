package me.retrodaredevil.action.lang;

public enum BooleanArgument implements Argument {
	TRUE(true),
	FALSE(false),
	;
	private final boolean value;

	BooleanArgument(boolean value) {
		this.value = value;
	}

	public static BooleanArgument get(boolean value) {
		return value ? TRUE : FALSE;
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value ? "true" : "false";
	}
}
