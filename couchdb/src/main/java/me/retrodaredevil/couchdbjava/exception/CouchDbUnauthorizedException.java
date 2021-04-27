package me.retrodaredevil.couchdbjava.exception;

import me.retrodaredevil.couchdbjava.CouchDbStatusCode;
import me.retrodaredevil.couchdbjava.response.ErrorResponse;
import org.jetbrains.annotations.Nullable;

public class CouchDbUnauthorizedException extends CouchDbCodeException {
	public CouchDbUnauthorizedException(@Nullable ErrorResponse errorResponse) {
		super(CouchDbStatusCode.UNAUTHORIZED, errorResponse);
	}

	public CouchDbUnauthorizedException(String message, @Nullable ErrorResponse errorResponse) {
		super(message, CouchDbStatusCode.UNAUTHORIZED, errorResponse);
	}

	public CouchDbUnauthorizedException(String message, Throwable cause, @Nullable ErrorResponse errorResponse) {
		super(message, cause, CouchDbStatusCode.UNAUTHORIZED, errorResponse);
	}

	public CouchDbUnauthorizedException(Throwable cause, @Nullable ErrorResponse errorResponse) {
		super(cause, CouchDbStatusCode.UNAUTHORIZED, errorResponse);
	}

	public CouchDbUnauthorizedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, @Nullable ErrorResponse errorResponse) {
		super(message, cause, enableSuppression, writableStackTrace, CouchDbStatusCode.UNAUTHORIZED, errorResponse);
	}
}
