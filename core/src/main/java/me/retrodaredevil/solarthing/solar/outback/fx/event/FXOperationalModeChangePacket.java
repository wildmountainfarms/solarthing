package me.retrodaredevil.solarthing.solar.outback.fx.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.ChangePacket;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.event.SupplementarySolarEventPacket;
import me.retrodaredevil.solarthing.solar.outback.OutbackData;
import me.retrodaredevil.solarthing.solar.outback.fx.OperationalMode;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;


@JsonDeserialize(as = ImmutableFXOperationalModeChangePacket.class)
@JsonTypeName("FX_OPERATIONAL_MODE_CHANGE")
@JsonExplicit
public interface FXOperationalModeChangePacket extends SupplementarySolarEventPacket, OutbackData, ChangePacket {
	@NotNull
    @Override
	default SolarEventPacketType getPacketType(){
		return SolarEventPacketType.FX_OPERATIONAL_MODE_CHANGE;
	}

	@JsonProperty("operationalModeValue")
	int getOperationalModeValue();
	@JsonProperty("previousOperationalModeValue")
	@Nullable Integer getPreviousOperationalModeValue();

	default OperationalMode getOperationalMode(){ return Modes.getActiveMode(OperationalMode.class, getOperationalModeValue()); }
	default @Nullable OperationalMode getPreviousOperationalMode(){
		Integer previous = getPreviousOperationalModeValue();
		if(previous == null){
			return null;
		}
		return Modes.getActiveMode(OperationalMode.class, previous);
	}

	@Override
	default boolean isLastUnknown() {
		return getPreviousOperationalModeValue() == null;
	}
}
