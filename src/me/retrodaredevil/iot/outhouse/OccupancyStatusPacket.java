package me.retrodaredevil.iot.outhouse;

import me.retrodaredevil.iot.packets.Packet;

public interface OccupancyStatusPacket extends Packet {
	/**
	 * @return The occupancy code representing the {@link Occupancy}
	 */
	int getOccupancy();
}
