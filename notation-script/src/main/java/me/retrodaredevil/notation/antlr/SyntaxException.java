package me.retrodaredevil.notation.antlr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SyntaxException extends Exception {
	private final List<SyntaxError> syntaxErrors;

	public SyntaxException(List<SyntaxError> syntaxErrors) {
		this.syntaxErrors = Collections.unmodifiableList(new ArrayList<>(syntaxErrors));
	}

	public List<SyntaxError> getSyntaxErrors() {
		return syntaxErrors;
	}
}
