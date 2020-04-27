package me.retrodaredevil.solarthing.packets.collection.parsing;

import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;

import javax.validation.constraints.NotNull;

public interface PacketGroupParser {
	@NotNull PacketGroup parse(ObjectNode objectNode) throws PacketParseException;
}
