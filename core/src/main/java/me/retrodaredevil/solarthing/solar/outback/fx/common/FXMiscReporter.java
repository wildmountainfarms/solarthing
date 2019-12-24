package me.retrodaredevil.solarthing.solar.outback.fx.common;

import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.outback.fx.MiscMode;

import java.util.Set;

public interface FXMiscReporter {
	int getMiscValue();
	default Set<MiscMode> getMiscModes(){ return Modes.getActiveModes(MiscMode.class, getMiscValue()); }
}
