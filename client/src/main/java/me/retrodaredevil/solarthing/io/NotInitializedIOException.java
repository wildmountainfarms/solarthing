package me.retrodaredevil.solarthing.io;

import java.io.IOException;

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
