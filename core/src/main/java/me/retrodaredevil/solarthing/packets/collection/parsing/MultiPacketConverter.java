package me.retrodaredevil.solarthing.packets.collection.parsing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class MultiPacketConverter implements JsonPacketParser {
	private final ObjectMapper mapper;

	private MultiPacketConverter(ObjectMapper mapper) {
		this.mapper = mapper;
	}
	@SafeVarargs
	public static MultiPacketConverter createFrom(ObjectMapper baseMapper, Class<? extends DocumentedPacket>... packetClasses) {
		return createFrom(baseMapper, Arrays.asList(packetClasses));
	}
	public static MultiPacketConverter createFrom(ObjectMapper baseMapper, Collection<? extends Class<? extends DocumentedPacket>> packetClasses) {
		ObjectMapper mapper = baseMapper.copy();
		mapper.setSubtypeResolver(baseMapper.getSubtypeResolver().copy());
		mapper.getSubtypeResolver().registerSubtypes(Collections.unmodifiableCollection(packetClasses));
		return new MultiPacketConverter(mapper);
	}

	@Override
	public @NotNull DocumentedPacket parsePacket(JsonNode packetNode) throws PacketParseException {
		try {
			return mapper.convertValue(packetNode, DocumentedPacket.class);
		} catch (IllegalArgumentException ex){
			throw new PacketParseException(ex);
		}
	}
}
