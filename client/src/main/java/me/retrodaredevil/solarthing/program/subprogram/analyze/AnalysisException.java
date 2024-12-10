package me.retrodaredevil.solarthing.program.subprogram.analyze;

import static java.util.Objects.requireNonNull;

public class AnalysisException extends Exception {
	public AnalysisException(String message, Throwable cause) {
		super(requireNonNull(message), requireNonNull(cause));
	}
}
