package me.retrodaredevil.solarthing.database.exception;

public class SolarThingDatabaseException extends Exception {
	public SolarThingDatabaseException() {
	}

	public SolarThingDatabaseException(String message) {
		super(message);
	}

	public SolarThingDatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public SolarThingDatabaseException(Throwable cause) {
		super(cause);
	}

	public SolarThingDatabaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
