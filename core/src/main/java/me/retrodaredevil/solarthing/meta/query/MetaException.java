package me.retrodaredevil.solarthing.meta.query;

public class MetaQueryException extends Exception {
	public MetaQueryException() {
	}

	public MetaQueryException(String message) {
		super(message);
	}

	public MetaQueryException(String message, Throwable cause) {
		super(message, cause);
	}

	public MetaQueryException(Throwable cause) {
		super(cause);
	}

	public MetaQueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
