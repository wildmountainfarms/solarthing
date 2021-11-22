package me.retrodaredevil.solarthing.database.exception;

/**
 * A database exception to represent that something was not found.
 * <p>
 * Note: You should check to make sure your implementation throws this if you are relying on changing behavior
 * depending on the type of exception thrown
 */
public class NotFoundSolarThingDatabaseException extends SolarThingDatabaseException {
	public NotFoundSolarThingDatabaseException() {
	}

	public NotFoundSolarThingDatabaseException(String message) {
		super(message);
	}

	public NotFoundSolarThingDatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundSolarThingDatabaseException(Throwable cause) {
		super(cause);
	}

	public NotFoundSolarThingDatabaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
