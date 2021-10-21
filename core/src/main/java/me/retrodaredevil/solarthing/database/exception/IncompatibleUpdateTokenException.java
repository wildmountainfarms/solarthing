package me.retrodaredevil.solarthing.database.exception;

public class IncompatibleUpdateTokenException extends SolarThingDatabaseRuntimeException {
	public IncompatibleUpdateTokenException() {
	}

	public IncompatibleUpdateTokenException(String message) {
		super(message);
	}

	public IncompatibleUpdateTokenException(String message, Throwable cause) {
		super(message, cause);
	}

	public IncompatibleUpdateTokenException(Throwable cause) {
		super(cause);
	}

	public IncompatibleUpdateTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
