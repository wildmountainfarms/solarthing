package me.retrodaredevil.solarthing.database.exception;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class UnauthorizedSolarThingDatabaseException extends SolarThingDatabaseException {
	public UnauthorizedSolarThingDatabaseException(String message) {
		super(message);
	}


	public UnauthorizedSolarThingDatabaseException(@Nullable String message, Throwable cause) {
		super(message, cause);
	}
}
