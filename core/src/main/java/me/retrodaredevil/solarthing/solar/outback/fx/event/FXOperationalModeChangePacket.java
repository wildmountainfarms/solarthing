package me.retrodaredevil.solarthing.solar.outback.fx.event;

import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.event.SupplementarySolarEventPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.OperationalMode;
import org.jetbrains.annotations.Nullable;

public interface FXOperationalModeChangePacket extends SupplementarySolarEventPacket {
	int getOperationalModeValue();
	@Nullable
	Integer getPreviousOperationalModeValue();

	default OperationalMode getOperationalMode(){ return Modes.getActiveMode(OperationalMode.class, getOperationalModeValue()); }
	@Nullable
	default OperationalMode getPreviousOperationalMode(){
		Integer previous = getPreviousOperationalModeValue();
		if(previous == null){
			return null;
		}
		return Modes.getActiveMode(OperationalMode.class, previous);
	}
}
