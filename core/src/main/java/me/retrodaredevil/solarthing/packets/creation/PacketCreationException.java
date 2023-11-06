package me.retrodaredevil.solarthing.packets.creation;

/**
 * A packet creation exception's cause usually should be null.
 * When its cause is null, the stacktrace should be irrelevant.
 * When there is a non-null cause, the stacktrace should be debugged only.
 * Do not use this exception if you need stacktrace information to be logged.
 */
public class PacketCreationException extends Exception {
	public PacketCreationException() {
	}

	public PacketCreationException(String message) {
		super(message);
	}

	public PacketCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public PacketCreationException(Throwable cause) {
		super(cause);
	}

	public PacketCreationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
