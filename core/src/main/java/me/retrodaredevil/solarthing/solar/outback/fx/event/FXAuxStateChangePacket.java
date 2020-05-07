package me.retrodaredevil.solarthing.solar.outback.fx.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.ChangePacket;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.event.SupplementarySolarEventPacket;
import me.retrodaredevil.solarthing.solar.outback.OutbackData;
import me.retrodaredevil.solarthing.annotations.Nullable;

import me.retrodaredevil.solarthing.annotations.NotNull;

@JsonDeserialize(as = ImmutableFXAuxStateChangePacket.class)
@JsonTypeName("FX_AUX_STATE_CHANGE")
@JsonExplicit
public interface FXAuxStateChangePacket extends SupplementarySolarEventPacket, OutbackData, ChangePacket {
	@NotNull
    @Override
	default SolarEventPacketType getPacketType(){
		return SolarEventPacketType.FX_AUX_STATE_CHANGE;
	}

	@JsonProperty("isAuxActive")
	boolean isAuxActive();
	@JsonProperty("wasAuxActive")
	@Nullable Boolean getAuxWasActive();

	@Override
	default boolean isLastUnknown() {
		return getAuxWasActive() == null;
	}
}
