package me.retrodaredevil.solarthing.packets.collection;

import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.packets.Packet;

public interface JsonPacketGetter {
	/**
	 *
	 * @param packetObject The {@link JsonObject} representing the packet
	 * @return The {@link Packet} represented by {@code packetObject}
	 * @throws me.retrodaredevil.solarthing.packets.UnknownPacketTypeException
	 */
	Packet createFromJson(JsonObject packetObject);
}
