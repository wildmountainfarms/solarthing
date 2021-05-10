package me.retrodaredevil.solarthing.packets.collection.parsing;

import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;

import me.retrodaredevil.solarthing.annotations.NotNull;

@Deprecated
public interface PacketGroupParser {
	@NotNull PacketGroup parse(ObjectNode objectNode) throws PacketParseException;
}
