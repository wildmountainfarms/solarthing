package me.retrodaredevil.solarthing.solar.renogy.rover.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.ChangePacket;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.event.SupplementarySolarEventPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.ChargingState;
import me.retrodaredevil.solarthing.solar.renogy.rover.SupplementaryRoverPacket;

@JsonDeserialize(as = ImmutableRoverChargingStateChangePacket.class)
@JsonTypeName("ROVER_CHARGING_STATE_CHANGE")
@JsonExplicit
public interface RoverChargingStateChangePacket extends SupplementarySolarEventPacket, SupplementaryRoverPacket, ChangePacket {

	@Override
	default @NotNull SolarEventPacketType getPacketType() {
		return SolarEventPacketType.ROVER_CHARGING_STATE_CHANGE;
	}

	@JsonProperty("chargingStateValue")
	int getChargingStateValue();

	@JsonProperty("previousChargingStateValue")
	@Nullable Integer getPreviousChargingStateValue();

	@Override
	default boolean isLastUnknown() {
		return getPreviousChargingStateValue() == null;
	}

	@GraphQLInclude("chargingMode")
	default @NotNull ChargingState getChargingMode() {
		return Modes.getActiveMode(ChargingState.class, getChargingStateValue());
	}
	@GraphQLInclude("previousChargingMode")
	default @Nullable ChargingState getPreviousChargingMode(){
		Integer previousChargingStateValue = getPreviousChargingStateValue();
		if(previousChargingStateValue == null){
			return null;
		}
		return Modes.getActiveMode(ChargingState.class, previousChargingStateValue);
	}
}
