package me.retrodaredevil.solarthing.solar;

import me.retrodaredevil.solarthing.packets.DocumentedPacket;

@SuppressWarnings("unused")
public interface SolarPacket extends DocumentedPacket<SolarPacketType> {

	/**
	 * Should be serialized as "address"
	 * @return [0..10] The address of the port that the device that sent this packet is plugged in to. If 0, this device is plugged directly into the Mate
	 */
	int getAddress();
}
