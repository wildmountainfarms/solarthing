package me.retrodaredevil.solarthing.solar.outback.fx.event;

import me.retrodaredevil.solarthing.solar.event.SupplementarySolarEventPacket;
import org.jetbrains.annotations.Nullable;

public interface FXAuxStateChangePacket extends SupplementarySolarEventPacket {
	boolean isAuxActive();
	@Nullable
	Boolean getAuxWasActive();
}
