package me.retrodaredevil.solarthing.solar.renogy.rover.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.ChangePacket;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.event.SupplementarySolarEventPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.SupplementaryRoverPacket;

/**
 * Represents a change in {@link RoverStatusPacket#getErrorModeValue()}. Note that this class does not contain
 * any convenience methods for getting a set of errors because the type of errors returned depends on data not present in
 * this packet.
 */
@JsonDeserialize(as = ImmutableRoverErrorModeChangePacket.class)
@JsonTypeName("ROVER_ERROR_MODE_CHANGE")
@JsonExplicit
public interface RoverErrorModeChangePacket extends SupplementarySolarEventPacket, SupplementaryRoverPacket, ChangePacket {

	@DefaultFinal
	@Override
	default @NotNull SolarEventPacketType getPacketType() {
		return SolarEventPacketType.ROVER_ERROR_MODE_CHANGE;
	}

	@JsonProperty("errorModeValue")
	int getErrorModeValue();

	@JsonProperty("previousErrorModeValue")
	@Nullable Integer getPreviousErrorModeValue();

	@Override
	default boolean isLastUnknown() {
		return getPreviousErrorModeValue() == null;
	}
}
