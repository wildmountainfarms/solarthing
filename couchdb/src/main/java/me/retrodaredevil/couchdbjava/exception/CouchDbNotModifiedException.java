package me.retrodaredevil.couchdbjava.exception;

import me.retrodaredevil.couchdbjava.CouchDbStatusCode;
import me.retrodaredevil.couchdbjava.response.ErrorResponse;
import org.jetbrains.annotations.Nullable;

public class CouchDbNotModifiedException extends CouchDbCodeException {
	public CouchDbNotModifiedException(@Nullable ErrorResponse errorResponse) {
		super(CouchDbStatusCode.NOT_MODIFIED, errorResponse);
	}

	public CouchDbNotModifiedException(String message, @Nullable ErrorResponse errorResponse) {
		super(message, CouchDbStatusCode.NOT_MODIFIED, errorResponse);
	}

	public CouchDbNotModifiedException(String message, Throwable cause, @Nullable ErrorResponse errorResponse) {
		super(message, cause, CouchDbStatusCode.NOT_MODIFIED, errorResponse);
	}

	public CouchDbNotModifiedException(Throwable cause, @Nullable ErrorResponse errorResponse) {
		super(cause, CouchDbStatusCode.NOT_MODIFIED, errorResponse);
	}

	public CouchDbNotModifiedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, @Nullable ErrorResponse errorResponse) {
		super(message, cause, enableSuppression, writableStackTrace, CouchDbStatusCode.NOT_MODIFIED, errorResponse);
	}
}
