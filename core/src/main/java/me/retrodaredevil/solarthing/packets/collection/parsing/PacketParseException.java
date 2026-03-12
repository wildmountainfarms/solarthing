package me.retrodaredevil.solarthing.packets.collection.parsing;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class PacketParseException extends Exception {
	public PacketParseException(String message) {
		super(message);
	}

	public PacketParseException(String message, Throwable cause) {
		super(message, cause);
	}
}
