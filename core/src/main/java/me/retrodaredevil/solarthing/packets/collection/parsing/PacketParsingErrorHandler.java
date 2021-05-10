package me.retrodaredevil.solarthing.packets.collection.parsing;

public interface PacketParsingErrorHandler {
	void handleError(Exception exception) throws PacketParseException;

	PacketParsingErrorHandler DO_NOTHING = (ex) -> {};
}
