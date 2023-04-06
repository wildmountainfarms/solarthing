package me.retrodaredevil.solarthing.packets.creation;

public class PacketTooBigException extends PacketCreationException {
	public PacketTooBigException() {
	}

	public PacketTooBigException(String message) {
		super(message);
	}

	public PacketTooBigException(String message, Throwable cause) {
		super(message, cause);
	}

	public PacketTooBigException(Throwable cause) {
		super(cause);
	}

	public PacketTooBigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
