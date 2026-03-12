package me.retrodaredevil.solarthing.packets.collection.parsing;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PacketParsingErrorHandler {
	void handleError(Exception exception) throws PacketParseException;

	PacketParsingErrorHandler DO_NOTHING = (ex) -> {};
}
