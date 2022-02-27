package me.retrodaredevil.solarthing.database.exception;

public class UnauthorizedSolarThingDatabaseException extends SolarThingDatabaseException {
	public UnauthorizedSolarThingDatabaseException() {
	}

	public UnauthorizedSolarThingDatabaseException(String message) {
		super(message);
	}

	public UnauthorizedSolarThingDatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnauthorizedSolarThingDatabaseException(Throwable cause) {
		super(cause);
	}

	public UnauthorizedSolarThingDatabaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
