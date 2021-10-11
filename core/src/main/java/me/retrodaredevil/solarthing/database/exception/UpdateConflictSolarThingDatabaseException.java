package me.retrodaredevil.solarthing.database.exception;

public class UpdateConflictSolarThingDatabaseException extends SolarThingDatabaseException {
	public UpdateConflictSolarThingDatabaseException() {
	}

	public UpdateConflictSolarThingDatabaseException(String message) {
		super(message);
	}

	public UpdateConflictSolarThingDatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public UpdateConflictSolarThingDatabaseException(Throwable cause) {
		super(cause);
	}

	public UpdateConflictSolarThingDatabaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
