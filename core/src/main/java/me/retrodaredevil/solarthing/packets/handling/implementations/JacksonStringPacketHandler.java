package me.retrodaredevil.solarthing.packets.handling.implementations;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import org.jspecify.annotations.NullMarked;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@NullMarked
public class JacksonStringPacketHandler implements StringPacketHandler {
	private final ObjectMapper mapper;

	public JacksonStringPacketHandler(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public String getString(PacketCollection packetCollection) {
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(packetCollection);
		} catch (JacksonException e) {
			throw new RuntimeException("Couldn't write to string!", e);
		}
	}
}
