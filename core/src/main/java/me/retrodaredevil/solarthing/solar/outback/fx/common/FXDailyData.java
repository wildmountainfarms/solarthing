package me.retrodaredevil.solarthing.solar.outback.fx.common;

import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.common.DailyBatteryVoltage;
import me.retrodaredevil.solarthing.solar.common.DailyData;
import me.retrodaredevil.solarthing.solar.common.ErrorReporter;
import me.retrodaredevil.solarthing.solar.outback.fx.ACMode;
import me.retrodaredevil.solarthing.solar.outback.fx.FXErrorMode;
import me.retrodaredevil.solarthing.solar.outback.fx.OperationalMode;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;

import java.util.Collection;
import java.util.Set;

public interface FXDailyData extends DailyBatteryVoltage, ErrorReporter, FXWarningReporter, FXMiscReporter {
	@Override
	default boolean isNewDay(DailyData previousDailyData){
		if (!(previousDailyData instanceof FXDailyData)) {
			throw new IllegalArgumentException("previousDailyData is not a FXDailyData! It's: " + previousDailyData.getClass().getName());
		}
		DailyFXPacket previous = (DailyFXPacket) previousDailyData;
		/*
		In the early versions of DailyFXPacket, startDateMillis was not serialized. In new versions, they are always serialized.
		This is why we have to perform a null check and deal with either one being null.
		 */
		Long dateMillis = getStartDateMillis();
		Long previousMillis = previous.getStartDateMillis();
		if(dateMillis != null && previousMillis != null){
			return dateMillis > previousMillis;
		}
		return getInverterKWH() < previous.getInverterKWH() ||
				getChargerKWH() < previous.getChargerKWH() ||
				getBuyKWH() < previous.getBuyKWH() ||
				getSellKWH() < previous.getSellKWH();
	}

	float getInverterKWH();
	float getChargerKWH();
	float getBuyKWH();
	float getSellKWH();

	Collection<Integer> getOperationalModeValues();
	default Set<OperationalMode> getOperationalModes(){ return Modes.getActiveModes(OperationalMode.class, getOperationalModeValues()); }

	/**
	 * Should be serialized as "errorModeValue"
	 * @return The bit-masked value representing all the error modes that were active during one day
	 */
	@Override
	int getErrorMode();
	@Override
	default Set<FXErrorMode> getErrorModes(){ return Modes.getActiveModes(FXErrorMode.class, getErrorMode()); }

	@Override
	int getWarningModeValue();

	@Override
	int getMiscValue();

	Collection<Integer> getACModeValues();
	default Set<ACMode> getACModes(){ return Modes.getActiveModes(ACMode.class, getACModeValues()); }
}
