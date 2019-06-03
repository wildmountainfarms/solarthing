package me.retrodaredevil.solarthing.outhouse;

public interface OccupancyPacket extends OuthousePacket {
	/**
	 * @return The occupancy code representing the {@link Occupancy}
	 */
	int getOccupancy();
}
