package me.retrodaredevil.solarthing.packets.collection.parsing;

public class PacketParseException extends Exception {
	public PacketParseException() {
	}

	public PacketParseException(String message) {
		super(message);
	}

	public PacketParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public PacketParseException(Throwable cause) {
		super(cause);
	}

	public PacketParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
