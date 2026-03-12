package me.retrodaredevil.solarthing.database.exception;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class SolarThingDatabaseRuntimeException extends RuntimeException {
	public SolarThingDatabaseRuntimeException(String message) {
		super(message);
	}

	public SolarThingDatabaseRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
