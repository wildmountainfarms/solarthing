package me.retrodaredevil.solarthing.packets.creation;

public class PacketTooSmallException extends PacketCreationException {
	public PacketTooSmallException() {
	}

	public PacketTooSmallException(String message) {
		super(message);
	}

	public PacketTooSmallException(String message, Throwable cause) {
		super(message, cause);
	}

	public PacketTooSmallException(Throwable cause) {
		super(cause);
	}

	public PacketTooSmallException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
