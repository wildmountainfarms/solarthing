package me.retrodaredevil.iot.outhouse;

public interface OccupancyPacket extends OuthousePacket {
	/**
	 * @return The occupancy code representing the {@link Occupancy}
	 */
	int getOccupancy();
}
