package me.retrodaredevil.solarthing.solar;

public interface OutbackPacket extends SolarPacket {
	/**
	 * Should be serialized as "address"
	 * @return [0..10] The address of the port that the device that sent this packet is plugged in to. If 0, this device is plugged directly into the Mate
	 */
	@SuppressWarnings("unused")
	int getAddress();
}
