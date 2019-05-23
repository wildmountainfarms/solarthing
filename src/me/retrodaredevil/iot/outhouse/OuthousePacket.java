package me.retrodaredevil.iot.outhouse;

import me.retrodaredevil.iot.packets.Packet;

public interface OuthousePacket extends Packet {
	/**
	 * Should be serialized as "packetType"
	 * @return The packet type
	 */
	OuthousePacketType getPacketType();
}
