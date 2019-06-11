package me.retrodaredevil.solarthing.outhouse;

import me.retrodaredevil.solarthing.packets.Modes;

@SuppressWarnings("unused")
public interface OccupancyPacket extends OuthousePacket {
	/**
	 * @return The occupancy code representing the {@link Occupancy}
	 */
	int getOccupancy();
	
	default Occupancy getOccupancyMode(){
		return Modes.getActiveMode(Occupancy.class, getOccupancy());
	}
}
