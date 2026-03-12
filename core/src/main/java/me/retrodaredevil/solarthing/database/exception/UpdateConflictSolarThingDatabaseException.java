package me.retrodaredevil.solarthing.database.exception;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class UpdateConflictSolarThingDatabaseException extends SolarThingDatabaseException {
	public UpdateConflictSolarThingDatabaseException(String message) {
		super(message);
	}

	public UpdateConflictSolarThingDatabaseException(@Nullable String message, Throwable cause) {
		super(message, cause);
	}
}
