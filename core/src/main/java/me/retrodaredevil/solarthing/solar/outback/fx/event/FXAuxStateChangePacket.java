package me.retrodaredevil.solarthing.solar.outback.fx.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.ChangePacket;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.event.SupplementarySolarEventPacket;
import me.retrodaredevil.solarthing.solar.outback.SupplementaryOutbackPacket;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@JsonDeserialize(as = ImmutableFXAuxStateChangePacket.class)
@JsonTypeName("FX_AUX_STATE_CHANGE")
@JsonExplicit
@NullMarked
public interface FXAuxStateChangePacket extends SupplementarySolarEventPacket, SupplementaryOutbackPacket, ChangePacket {
	// TODO remove NonNull
	@DefaultFinal
	@Override
	default @NonNull SolarEventPacketType getPacketType(){
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
