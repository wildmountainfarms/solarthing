package me.retrodaredevil.solarthing.solar.outback.fx.common;

import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.outback.fx.WarningMode;

import java.util.Set;

public interface FXWarningReporter {
	int getWarningModeValue();
	default Set<WarningMode> getWarningModes(){ return Modes.getActiveModes(WarningMode.class, getWarningModeValue()); }
}
