package me.retrodaredevil.solarthing.io;

import org.jspecify.annotations.NullMarked;

import java.io.IOException;

@NullMarked
public class NotInitializedIOException extends IOException {
	public NotInitializedIOException() {
	}

	public NotInitializedIOException(String message) {
		super(message);
	}

	public NotInitializedIOException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotInitializedIOException(Throwable cause) {
		super(cause);
	}
}
