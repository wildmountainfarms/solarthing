package me.retrodaredevil.notation.antlr;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.List;

public class ParseErrorListener extends BaseErrorListener {

	private final List<SyntaxError> syntaxErrors = new ArrayList<>();

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
		SyntaxError error = new SyntaxError(line, charPositionInLine, msg);
		syntaxErrors.add(error);
	}
	public List<SyntaxError> getSyntaxErrors() {
		return syntaxErrors;
	}

}
