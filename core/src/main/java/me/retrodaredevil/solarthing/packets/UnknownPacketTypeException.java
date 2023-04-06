package me.retrodaredevil.solarthing.packets;

public class UnknownPacketTypeException extends RuntimeException {
	public UnknownPacketTypeException() {
	}

	public UnknownPacketTypeException(String message) {
		super(message);
	}

	public UnknownPacketTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownPacketTypeException(Throwable cause) {
		super(cause);
	}

	public UnknownPacketTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
