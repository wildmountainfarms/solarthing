package me.retrodaredevil.solarthing.solar.outback.fx.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.event.SupplementarySolarEventPacket;
import me.retrodaredevil.solarthing.solar.outback.OutbackData;
import me.retrodaredevil.solarthing.solar.outback.fx.WarningMode;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXWarningReporter;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Set;

@JsonDeserialize(as = ImmutableFXWarningModeChangePacket.class)
@JsonTypeName("FX_WARNING_MODE_CHANGE")
@JsonExplicit
public interface FXWarningModeChangePacket extends SupplementarySolarEventPacket, OutbackData, FXWarningReporter {
	int DEFAULT_IGNORED_WARNING_MODE_VALUE_CONSTANT = 0;

	@NotNull
    @Override
	default SolarEventPacketType getPacketType(){
		return SolarEventPacketType.FX_WARNING_MODE_CHANGE;
	}

	@JsonProperty("warningModeValue")
	@Override
	int getWarningModeValue();
	@JsonProperty("previousWarningModeValue")
	Integer getPreviousWarningModeValue();

	@JsonProperty("ignoredWarningModeValueConstant")
	int getIgnoredWarningModeValueConstant();

	default @Nullable Set<WarningMode> getPreviousWarningModes(){
		Integer previousWarningModeValue = getPreviousWarningModeValue();
		if(previousWarningModeValue == null){
			return null;
		}
		return Modes.getActiveModes(WarningMode.class, previousWarningModeValue);
	}
}
