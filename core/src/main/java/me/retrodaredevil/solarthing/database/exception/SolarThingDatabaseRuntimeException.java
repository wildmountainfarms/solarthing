package me.retrodaredevil.solarthing.database.exception;

public class SolarThingDatabaseRuntimeException extends RuntimeException {
	public SolarThingDatabaseRuntimeException() {
	}

	public SolarThingDatabaseRuntimeException(String message) {
		super(message);
	}

	public SolarThingDatabaseRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public SolarThingDatabaseRuntimeException(Throwable cause) {
		super(cause);
	}

	public SolarThingDatabaseRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
