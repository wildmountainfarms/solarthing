package me.retrodaredevil.solarthing.outhouse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.packets.Modes;

@JsonDeserialize(as = ImmutableOccupancyPacket.class)
public interface OccupancyPacket extends OuthousePacket {
	/**
	 * @return The occupancy code representing the {@link Occupancy}
	 */
	@JsonProperty("occupancy")
	int getOccupancy();

	@SuppressWarnings("unused")
	default Occupancy getOccupancyMode(){
		return Modes.getActiveMode(Occupancy.class, getOccupancy());
	}
}
