package me.retrodaredevil.solarthing.solar.outback.fx.event;

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
import me.retrodaredevil.solarthing.solar.outback.SupplementaryOutbackPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.ACMode;

@JsonDeserialize(as = ImmutableFXACModeChangePacket.class)
@JsonTypeName("FX_AC_MODE_CHANGE")
@JsonExplicit
public interface FXACModeChangePacket extends SupplementarySolarEventPacket, SupplementaryOutbackPacket, ChangePacket {
	@Override
	default @NotNull SolarEventPacketType getPacketType(){
		return SolarEventPacketType.FX_AC_MODE_CHANGE;
	}

	@JsonProperty("acModeValue")
	int getACModeValue();
	@JsonProperty("previousACModeValue")
	@Nullable Integer getPreviousACModeValue();

	@GraphQLInclude("acMode")
	default @NotNull ACMode getACMode(){ return Modes.getActiveMode(ACMode.class, getACModeValue()); }

	@GraphQLInclude("previousACMode")
	default @Nullable ACMode getPreviousACMode(){
		Integer previous = getPreviousACModeValue();
		if(previous == null){
			return null;
		}
		return Modes.getActiveMode(ACMode.class, previous);
	}

	@Override
	default boolean isLastUnknown() {
		return getPreviousACModeValue() == null;
	}
}
