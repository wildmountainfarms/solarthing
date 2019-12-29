package me.retrodaredevil.solarthing.solar.outback.fx.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.event.SupplementarySolarEventPacket;
import me.retrodaredevil.solarthing.solar.outback.OutbackData;
import me.retrodaredevil.solarthing.solar.outback.fx.ACMode;
import org.jetbrains.annotations.Nullable;

@JsonDeserialize(as = ImmutableFXACModeChangePacket.class)
@JsonTypeName("FX_AC_MODE_CHANGE")
@JsonExplicit
public interface FXACModeChangePacket extends SupplementarySolarEventPacket, OutbackData {
	@JsonProperty("acModeValue")
	int getACModeValue();
	@JsonProperty("previousACModeValue")
	@Nullable
	Integer getPreviousACModeValue();

	default ACMode getACMode(){ return Modes.getActiveMode(ACMode.class, getACModeValue()); }
	@Nullable
	default ACMode getPreviousACMode(){
		Integer previous = getPreviousACModeValue();
		if(previous == null){
			return null;
		}
		return Modes.getActiveMode(ACMode.class, previous);
	}
}
