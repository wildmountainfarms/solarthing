package me.retrodaredevil.solarthing.packets.creation;

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
