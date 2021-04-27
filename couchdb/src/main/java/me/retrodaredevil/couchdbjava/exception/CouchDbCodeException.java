package me.retrodaredevil.couchdbjava.exception;

import me.retrodaredevil.couchdbjava.response.ErrorResponse;
import org.jetbrains.annotations.Nullable;

public class CouchDbCodeException extends CouchDbException {
	private final int code;
	private final @Nullable ErrorResponse errorResponse;

	public CouchDbCodeException(int code, @Nullable ErrorResponse errorResponse) {
		this.code = code;
		this.errorResponse = errorResponse;
	}

	public CouchDbCodeException(String message, int code, @Nullable ErrorResponse errorResponse) {
		super(message);
		this.code = code;
		this.errorResponse = errorResponse;
	}

	public CouchDbCodeException(String message, Throwable cause, int code, @Nullable ErrorResponse errorResponse) {
		super(message, cause);
		this.code = code;
		this.errorResponse = errorResponse;
	}

	public CouchDbCodeException(Throwable cause, int code, @Nullable ErrorResponse errorResponse) {
		super(cause);
		this.code = code;
		this.errorResponse = errorResponse;
	}

	public CouchDbCodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code, @Nullable ErrorResponse errorResponse) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.code = code;
		this.errorResponse = errorResponse;
	}

	public int getCode() {
		return code;
	}

	public @Nullable ErrorResponse getErrorResponse() {
		return errorResponse;
	}
}
