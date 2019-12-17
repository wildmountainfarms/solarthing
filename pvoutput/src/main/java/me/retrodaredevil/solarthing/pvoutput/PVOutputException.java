package me.retrodaredevil.solarthing.pvoutput;

public class PVOutputException extends Exception {
	public PVOutputException() {
	}

	public PVOutputException(String message) {
		super(message);
	}

	public PVOutputException(String message, Throwable cause) {
		super(message, cause);
	}

	public PVOutputException(Throwable cause) {
		super(cause);
	}

	public PVOutputException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
