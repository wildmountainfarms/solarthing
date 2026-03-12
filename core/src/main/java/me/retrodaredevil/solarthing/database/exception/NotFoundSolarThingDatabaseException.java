package me.retrodaredevil.solarthing.database.exception;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * A database exception to represent that something was not found.
 * <p>
 * Note: You should check to make sure your implementation throws this if you are relying on changing behavior
 * depending on the type of exception thrown
 */
@NullMarked
public class NotFoundSolarThingDatabaseException extends SolarThingDatabaseException {
	public NotFoundSolarThingDatabaseException(String message) {
		super(message);
	}

	public NotFoundSolarThingDatabaseException(@Nullable String message, Throwable cause) {
		super(message, cause);
	}
}
