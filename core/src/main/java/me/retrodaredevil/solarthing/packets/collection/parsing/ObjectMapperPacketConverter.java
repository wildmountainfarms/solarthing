package me.retrodaredevil.solarthing.packets.collection.parsing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.packets.Packet;
import javax.validation.constraints.NotNull;

public class ObjectMapperPacketConverter implements JsonPacketParser {
	private final ObjectMapper mapper;
	private final Class<? extends Packet> convertTo;

	public ObjectMapperPacketConverter(ObjectMapper mapper, Class<? extends Packet> convertTo) {
		this.mapper = mapper;
		this.convertTo = convertTo;
	}

	@Override
	public @NotNull Packet parsePacket(JsonNode packetNode) throws PacketParseException {
		// This will never return null
		try {
			return mapper.convertValue(packetNode, convertTo);
		} catch (IllegalArgumentException ex){
			throw new PacketParseException(ex);
		}
	}
}
