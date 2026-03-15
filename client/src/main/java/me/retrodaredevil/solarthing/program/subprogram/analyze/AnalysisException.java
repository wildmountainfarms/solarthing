package me.retrodaredevil.solarthing.program.subprogram.analyze;

import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@NullMarked
public class AnalysisException extends Exception {
	public AnalysisException(String message, Throwable cause) {
		super(requireNonNull(message), requireNonNull(cause));
	}

	@Override
	public String getMessage() {
		return requireNonNull(super.getMessage());
	}

	@Override
	public Throwable getCause() {
		return requireNonNull(super.getCause());
	}
}
