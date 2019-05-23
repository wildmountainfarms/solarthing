package me.retrodaredevil.iot.solar;

import me.retrodaredevil.iot.packets.Packet;

public interface SolarPacket extends Packet {
	/**
	 * Should be serialized as "packetType"
	 * @return The packet type
	 */
	SolarPacketType getPacketType();

	/**
	 * Should be serialized as "address"
	 * @return [1..10] The address of the port that the device that sent this packet is plugged in to
	 */
	int getAddress();
}
