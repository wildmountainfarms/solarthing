package me.retrodaredevil.solarthing.database.exception;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class IncompatibleUpdateTokenException extends SolarThingDatabaseRuntimeException {
	public IncompatibleUpdateTokenException(String message) {
		super(message);
	}

	public IncompatibleUpdateTokenException(String message, Throwable cause) {
		super(message, cause);
	}
}
