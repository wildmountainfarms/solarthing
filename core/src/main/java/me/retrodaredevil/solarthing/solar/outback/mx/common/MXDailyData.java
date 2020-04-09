package me.retrodaredevil.solarthing.solar.outback.mx.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.packets.support.Support;
import me.retrodaredevil.solarthing.solar.common.DailyBatteryVoltage;
import me.retrodaredevil.solarthing.solar.common.DailyChargeController;
import me.retrodaredevil.solarthing.solar.common.DailyData;
import me.retrodaredevil.solarthing.solar.common.ErrorReporter;
import me.retrodaredevil.solarthing.solar.outback.OutbackData;
import me.retrodaredevil.solarthing.solar.outback.mx.MXErrorMode;
import javax.validation.constraints.NotNull;

import java.util.Set;

public interface MXDailyData extends OutbackData, DailyChargeController, DailyBatteryVoltage, ErrorReporter {
	@Override
	default boolean isNewDay(DailyData previousDailyData){
		if(!(previousDailyData instanceof MXDailyData)){
			throw new IllegalArgumentException("previousDailyData is not a MXDailyData! It's: " + previousDailyData.getClass().getName());
		}
		MXDailyData previous = (MXDailyData) previousDailyData;

		long dateMillis = getStartDateMillis();
		long previousMillis = previous.getStartDateMillis();
		return dateMillis > previousMillis;
	}

	@JsonProperty("errorModeValue")
	@Override
	int getErrorModeValue();
	@Override
	default Set<MXErrorMode> getErrorModes(){ return Modes.getActiveModes(MXErrorMode.class, getErrorModeValue()); }

	@JsonProperty("startDateMillis")
	@Override
	@NotNull
	Long getStartDateMillis();

	@NotNull
	@JsonProperty("dailyAHSupport")
	@Override
	Support getDailyAHSupport();

}

