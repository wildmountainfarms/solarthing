package me.retrodaredevil.notation.antlr;

import static java.util.Objects.requireNonNull;

public final class SyntaxError {
	private final int line;
	private final int charPositionInLine;
	private final String message;

	public SyntaxError(int line, int charPositionInLine, String message) {
		this.line = line;
		this.charPositionInLine = charPositionInLine;
		this.message = requireNonNull(message);
	}

	@Override
	public String toString() {
		return "line " + line + ":" + charPositionInLine + " " + message;
	}

	public int getLine() {
		return line;
	}

	public int getCharPositionInLine() {
		return charPositionInLine;
	}

	public String getMessage() {
		return message;
	}
}
