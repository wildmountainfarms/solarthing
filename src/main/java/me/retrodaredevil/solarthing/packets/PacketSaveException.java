package me.retrodaredevil.solarthing.packets;

public class PacketSaveException extends Exception {
	public PacketSaveException() {
	}
	
	public PacketSaveException(String message) {
		super(message);
	}
	
	public PacketSaveException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PacketSaveException(Throwable cause) {
		super(cause);
	}
	
	public PacketSaveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
