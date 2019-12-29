package me.retrodaredevil.solarthing.solar.renogy.rover;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.solar.SolarStatusPacketType;
import me.retrodaredevil.solarthing.solar.renogy.RenogyPacket;

/**
 * Represents a Rover Status Packet. This implements {@link RoverReadTable}
 */
@JsonDeserialize(as = ImmutableRoverStatusPacket.class)
@JsonTypeName("RENOGY_ROVER_STATUS")
@JsonExplicit
public interface RoverStatusPacket extends RenogyPacket, RoverReadTable {
	@Override
	default SolarStatusPacketType getPacketType(){
		return SolarStatusPacketType.RENOGY_ROVER_STATUS;
	}

	@Override
	RoverIdentifier getIdentifier();
}
