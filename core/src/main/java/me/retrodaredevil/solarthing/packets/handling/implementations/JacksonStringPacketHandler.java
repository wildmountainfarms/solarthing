package me.retrodaredevil.solarthing.packets.handling.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;

public class JacksonStringPacketHandler implements StringPacketHandler {
	private final ObjectMapper mapper;

	public JacksonStringPacketHandler(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public String getString(PacketCollection packetCollection) {
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(packetCollection);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Couldn't write to string!", e);
		}
	}
}
