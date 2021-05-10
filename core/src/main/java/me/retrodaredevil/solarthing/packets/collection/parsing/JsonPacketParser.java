package me.retrodaredevil.solarthing.packets.collection.parsing;

import com.fasterxml.jackson.databind.JsonNode;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.annotations.Nullable;

@Deprecated
public interface JsonPacketParser {
	/**
	 * Note: This is meant to return null and throw an exception because callers can handle both of these outcomes differently.
	 * Typically, if this returns null, you would ignore the value and carry on. However, if this throws an exception, you would normally let it rise up the call
	 * chain until something can handle it. A good example of how this is used is {@link SimplePacketGroupParser}. {@link SimplePacketGroupParser} ignores
	 * null values and will also let exceptions go up the call chain. When this is not desired, {@link LenientPacketParser} or a variant of {@link PacketParserMultiplexer}
	 * can return null instead of throwing an exception.
	 *
	 * @param packetNode The json node to parse a packet from
	 * @return The parsed {@link Packet} or null.
	 * @throws PacketParseException thrown if this is unable to parse {@code packetNode}
	 */
	@Nullable
	Packet parsePacket(JsonNode packetNode) throws PacketParseException;
}
