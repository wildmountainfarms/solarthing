package me.retrodaredevil.solarthing.packets.creation;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class PacketTooBigException extends PacketCreationException {
	public PacketTooBigException(String message) {
		super(message);
	}
}
