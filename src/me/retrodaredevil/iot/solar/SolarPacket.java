package me.retrodaredevil.iot.solar;

import me.retrodaredevil.iot.packets.DocumentedPacket;

public interface SolarPacket extends DocumentedPacket<SolarPacketType> {

	/**
	 * Should be serialized as "address"
	 * @return [1..10] The address of the port that the device that sent this packet is plugged in to
	 */
	int getAddress();
}
