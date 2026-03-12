package me.retrodaredevil.solarthing.database.exception;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class SolarThingDatabaseException extends Exception {
	public SolarThingDatabaseException(String message) {
		super(message);
	}

	// Note: message is only nullable for compatibility reasons - TODO make message non-null
	public SolarThingDatabaseException(@Nullable String message, Throwable cause) {
		super(message, cause);
	}
}
