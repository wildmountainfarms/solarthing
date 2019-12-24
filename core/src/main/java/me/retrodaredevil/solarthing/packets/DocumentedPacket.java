package me.retrodaredevil.solarthing.packets;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "packetType") // now packetType is automatically serialized
public interface DocumentedPacket<T extends Enum<T> & DocumentedPacketType> extends Packet {
	/**
	 * Should be serialized as "packetType"
	 * @return The packet type
	 */
	T getPacketType();
}
