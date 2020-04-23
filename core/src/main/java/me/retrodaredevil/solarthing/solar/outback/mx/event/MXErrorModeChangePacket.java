package me.retrodaredevil.solarthing.solar.outback.mx.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.ChangePacket;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.common.ErrorReporter;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.event.SupplementarySolarEventPacket;
import me.retrodaredevil.solarthing.solar.outback.OutbackData;
import me.retrodaredevil.solarthing.solar.outback.mx.MXErrorMode;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Set;

@JsonDeserialize(as = ImmutableMXErrorModeChangePacket.class)
@JsonTypeName("MXFM_ERROR_MODE_CHANGE")
@JsonExplicit
public interface MXErrorModeChangePacket extends SupplementarySolarEventPacket, OutbackData, ErrorReporter, ChangePacket {
	@NotNull
    @Override
	default SolarEventPacketType getPacketType(){
		return SolarEventPacketType.MXFM_ERROR_MODE_CHANGE;
	}

	@JsonProperty("errorModeValue")
	@Override
	int getErrorModeValue();

	@JsonProperty("previousErrorModeValue")
	@Nullable Integer getPreviousErrorModeValue();

	@Override
	default Set<MXErrorMode> getErrorModes(){
		return Modes.getActiveModes(MXErrorMode.class, getErrorModeValue());
	}
	default @Nullable Set<MXErrorMode> getPreviousErrorModes(){
		Integer previousErrorModeValue = getPreviousErrorModeValue();
		if(previousErrorModeValue == null){
			return null;
		}
		return Modes.getActiveModes(MXErrorMode.class, previousErrorModeValue);
	}

	@Override
	default boolean isLastUnknown() {
		return getPreviousErrorModeValue() == null;
	}
}
