package me.retrodaredevil.solarthing.solar.outback.mx.common;

import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.common.DailyChargeController;
import me.retrodaredevil.solarthing.solar.common.ErrorReporter;
import me.retrodaredevil.solarthing.solar.outback.mx.MXErrorMode;

import java.util.Set;

public interface MXDailyData extends DailyChargeController, ErrorReporter {
	@Override
	int getErrorModeValue();
	@Override
	default Set<MXErrorMode> getErrorModes(){ return Modes.getActiveModes(MXErrorMode.class, getErrorModeValue()); }
}

