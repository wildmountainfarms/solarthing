package me.retrodaredevil.solarthing.solar.renogy.rover;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.packets.VersionedPacket;
import me.retrodaredevil.solarthing.packets.identification.NumberedIdentifiable;
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
public interface RoverStatusPacket extends RenogyPacket, RoverReadTable, VersionedPacket, NumberedIdentifiable {


	@UtilityClass
	class Version {
		private Version() {throw new UnsupportedOperationException();}

		/** The version of rover status packets that have correct values for two register values. (Bug fixed 2021.02.18)*/
		public static final int CORRECT_TWO_REGISTER = 2;
		/** The version of rover status packets that may have a "number" attached to them. Represents packets from SolarThing 2021.5.0 an onwards */
		public static final int NUMBERED_IDENTIFIER = 3;
		/** The version of rover status packets that no longer contain convenience fields. Note that the value of this is actually the same as {@link #NUMBERED_IDENTIFIER}.
		 * This means it is not possible to distinguish between {@link #NUMBERED_IDENTIFIER} and this version packets only by looking at the packetVersion value. */
		public static final int REMOVED_CONVENIENCE_FIELDS = 3;
		/** Represents the value that {@link #REMOVED_CONVENIENCE_FIELDS} had before being changed back to {@link #NUMBERED_IDENTIFIER} because we never actually
		 * saved the desired value in the packet */
		@Deprecated
		public static final int REMOVED_CONVENIENCE_FIELDS_BAD_VALUE_NEVER_USED = 4;
		/** Removed softwareVersionString convenience field and it is guaranteed that all other convenience fields are gone.*/
		public static final int REMOVED_CONVENIENCE_FIELDS_2 = 5;
		/** Version to indicate that there may also be charging state change events in the events database. Should only be present for a short period of time on WMF's database*/
		public static final int ADDED_CHARGING_STATE_EVENT = 6;
		public static final int ADDED_ERROR_MODE_EVENT = 7;
		public static final int LATEST = ADDED_ERROR_MODE_EVENT;
	}

	@DefaultFinal
	@Override
	default @NotNull SolarStatusPacketType getPacketType(){
		return SolarStatusPacketType.RENOGY_ROVER_STATUS;
	}

	@Override
	@NotNull RoverIdentifier getIdentifier();

	@JsonInclude(JsonInclude.Include.NON_DEFAULT) // won't include 0
	@JsonProperty("number")
	@Override
	int getNumber();
}
