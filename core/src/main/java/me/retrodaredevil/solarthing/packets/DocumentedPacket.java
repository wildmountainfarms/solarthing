package me.retrodaredevil.solarthing.packets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@JsonPropertyOrder({"packetType"}) // we want packetType to always be at the top
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "packetType")
@NullMarked
public interface DocumentedPacket extends Packet {
	// TODO remove NonNull
	/**
	 * Should be serialized as "packetType"
	 * @return The packet type
	 */
	@JsonProperty(value = "packetType")
	@NonNull DocumentedPacketType getPacketType();
}
