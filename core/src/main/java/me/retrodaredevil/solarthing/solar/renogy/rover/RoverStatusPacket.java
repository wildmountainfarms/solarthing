package me.retrodaredevil.solarthing.solar.renogy.rover;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.solar.renogy.RenogyPacket;

/**
 * Represents a Rover Status Packet. This implements {@link RoverReadTable}
 */
@JsonTypeName("RENOGY_ROVER_STATUS")
public interface RoverStatusPacket extends RenogyPacket, RoverReadTable {

	@Override
	RoverIdentifier getIdentifier();
}
