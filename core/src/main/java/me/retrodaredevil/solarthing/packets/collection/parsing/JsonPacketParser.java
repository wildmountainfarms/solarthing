package me.retrodaredevil.solarthing.packets.collection.parsing;

import com.fasterxml.jackson.databind.JsonNode;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.annotations.Nullable;

public interface JsonPacketParser {
	@Nullable
	Packet parsePacket(JsonNode packetNode) throws PacketParseException;
}
