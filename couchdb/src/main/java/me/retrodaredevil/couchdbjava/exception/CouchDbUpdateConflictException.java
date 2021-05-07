package me.retrodaredevil.couchdbjava.exception;

import me.retrodaredevil.couchdbjava.CouchDbStatusCode;
import me.retrodaredevil.couchdbjava.response.ErrorResponse;
import org.jetbrains.annotations.Nullable;

public class CouchDbUpdateConflictException extends CouchDbCodeException {
	public CouchDbUpdateConflictException(@Nullable ErrorResponse errorResponse) {
		super(CouchDbStatusCode.UPDATE_CONFLICT, errorResponse);
	}

	public CouchDbUpdateConflictException(String message, @Nullable ErrorResponse errorResponse) {
		super(message, CouchDbStatusCode.UPDATE_CONFLICT, errorResponse);
	}

	public CouchDbUpdateConflictException(String message, Throwable cause, @Nullable ErrorResponse errorResponse) {
		super(message, cause, CouchDbStatusCode.UPDATE_CONFLICT, errorResponse);
	}

	public CouchDbUpdateConflictException(Throwable cause, @Nullable ErrorResponse errorResponse) {
		super(cause, CouchDbStatusCode.UPDATE_CONFLICT, errorResponse);
	}

	public CouchDbUpdateConflictException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, @Nullable ErrorResponse errorResponse) {
		super(message, cause, enableSuppression, writableStackTrace, CouchDbStatusCode.UPDATE_CONFLICT, errorResponse);
	}
}
