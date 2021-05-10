package me.retrodaredevil.solarthing.packets.collection.parsing;

import com.fasterxml.jackson.databind.JsonNode;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.Packet;

@Deprecated
public class LenientPacketParser implements JsonPacketParser {
	private final JsonPacketParser parser;

	public LenientPacketParser(JsonPacketParser parser) {
		this.parser = parser;
	}

	@Override
	public @Nullable Packet parsePacket(JsonNode packetNode) {
		try {
			return parser.parsePacket(packetNode);
		} catch (PacketParseException e) {
			return null;
		}
	}
}
