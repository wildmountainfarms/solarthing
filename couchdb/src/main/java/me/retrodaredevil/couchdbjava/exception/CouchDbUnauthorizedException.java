package me.retrodaredevil.couchdbjava.exception;

public class CouchDbUnauthorizedException extends CouchDbException {
	public CouchDbUnauthorizedException() {
	}

	public CouchDbUnauthorizedException(String message) {
		super(message);
	}

	public CouchDbUnauthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

	public CouchDbUnauthorizedException(Throwable cause) {
		super(cause);
	}

	public CouchDbUnauthorizedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
