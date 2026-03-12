package me.retrodaredevil.solarthing.packets.handling;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class PacketHandleException extends Exception {

	public PacketHandleException(String message) {
		super(message);
	}

	public PacketHandleException(String message, Throwable cause) {
		super(message, cause);
	}
}
