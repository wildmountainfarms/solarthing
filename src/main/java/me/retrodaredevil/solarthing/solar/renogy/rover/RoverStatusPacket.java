package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.solar.renogy.RenogyPacket;

/**
 * Represents a Rover Status Packet. This implements {@link RoverReadTable}
 */
public interface RoverStatusPacket extends RenogyPacket, RoverReadTable {
	@Deprecated
	@Override
	default int getAddress(){
		return getControllerDeviceAddress();
	}
}
