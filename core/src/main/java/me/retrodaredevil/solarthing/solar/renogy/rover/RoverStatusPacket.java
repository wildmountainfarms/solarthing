package me.retrodaredevil.solarthing.solar.renogy.rover;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.VersionedPacket;
import me.retrodaredevil.solarthing.solar.SolarStatusPacketType;
import me.retrodaredevil.solarthing.solar.renogy.RenogyPacket;

/**
 * Represents a Rover Status Packet. This implements {@link RoverReadTable}
 * <p>
 * Note that this may also represent data from other Renogy products such as a Renogy Wanderer or Renogy Adventurer.
 * This has the ability to represent data for any device that can interact with Renogy's BT-1 Module and possibly future iterations
 * of their products that support Modbus Serial communication.
 * <p>
 * This is also compatible with all SRNE Solar Rebranded Products
 */
@JsonDeserialize(as = ImmutableRoverStatusPacket.class)
@JsonTypeName("RENOGY_ROVER_STATUS")
@JsonExplicit
@JsonClassDescription("Status packet for Rover and Rover-like devices")
public interface RoverStatusPacket extends RenogyPacket, RoverReadTable, VersionedPacket {

	/** The version of rover status packets that have correct values for two register values. (Bug fixed 2021.02.18)*/
	int VERSION_CORRECT_TWO_REGISTER = 2;

	@NotNull
	@Override
	default SolarStatusPacketType getPacketType(){
		return SolarStatusPacketType.RENOGY_ROVER_STATUS;
	}

}
