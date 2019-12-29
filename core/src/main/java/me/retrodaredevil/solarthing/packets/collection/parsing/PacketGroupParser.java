package me.retrodaredevil.solarthing.packets.collection.parsing;

import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;

public interface PacketGroupParser {
	PacketGroup parse(ObjectNode objectNode) throws PacketParseException;
}
