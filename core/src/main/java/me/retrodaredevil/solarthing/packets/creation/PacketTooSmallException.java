package me.retrodaredevil.solarthing.packets.creation;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class PacketTooSmallException extends PacketCreationException {
	public PacketTooSmallException(String message) {
		super(message);
	}
}
