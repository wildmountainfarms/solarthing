package me.retrodaredevil.solarthing.packets.creation;

public class PacketTooBigException extends PacketCreationException {
	public PacketTooBigException() {
	}

	public PacketTooBigException(String message) {
		super(message);
	}

}
