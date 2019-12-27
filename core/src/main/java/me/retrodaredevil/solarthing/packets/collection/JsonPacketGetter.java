package me.retrodaredevil.solarthing.packets.collection;

import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.packets.Packet;

@Deprecated
public interface JsonPacketGetter {
	/**
	 *
	 * @param packetObject The {@link JsonObject} representing the packet
	 * @return The {@link Packet} represented by {@code packetObject}
	 * @throws me.retrodaredevil.solarthing.packets.UnknownPacketTypeException if the type of the packet is unknown
	 */
	Packet createFromJson(JsonObject packetObject);
}
