package me.retrodaredevil.solarthing.rest.exceptions;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class RestException extends RuntimeException {
	public RestException(String message) {
		super(message);
	}

	public RestException(String message, Throwable cause) {
		super(message, cause);
	}

	public RestException(Throwable cause) {
		super(cause);
	}
}
