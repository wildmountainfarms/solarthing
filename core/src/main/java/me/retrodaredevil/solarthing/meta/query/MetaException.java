package me.retrodaredevil.solarthing.meta.query;

public class MetaException extends Exception {
	public MetaException() {
	}

	public MetaException(String message) {
		super(message);
	}

	public MetaException(String message, Throwable cause) {
		super(message, cause);
	}

	public MetaException(Throwable cause) {
		super(cause);
	}

	public MetaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
