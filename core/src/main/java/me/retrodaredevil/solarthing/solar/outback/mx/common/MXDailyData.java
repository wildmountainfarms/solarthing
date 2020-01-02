package me.retrodaredevil.solarthing.solar.outback.mx.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.packets.support.Support;
import me.retrodaredevil.solarthing.solar.common.DailyChargeController;
import me.retrodaredevil.solarthing.solar.common.ErrorReporter;
import me.retrodaredevil.solarthing.solar.outback.mx.MXErrorMode;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface MXDailyData extends DailyChargeController, ErrorReporter {
	@JsonProperty("errorModeValue")
	@Override
	int getErrorModeValue();
	@Override
	default Set<MXErrorMode> getErrorModes(){ return Modes.getActiveModes(MXErrorMode.class, getErrorModeValue()); }

	@JsonProperty("startDateMillis")
	@Override
	@NotNull
	Long getStartDateMillis();

	@JsonProperty("dailyAHSupport")
	@Override
	Support getDailyAHSupport();

}

