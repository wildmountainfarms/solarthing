package me.retrodaredevil.solarthing.solar.outback.fx.supplementary;

import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.common.DailyBatteryVoltage;
import me.retrodaredevil.solarthing.solar.common.DailyData;
import me.retrodaredevil.solarthing.solar.outback.fx.*;
import me.retrodaredevil.solarthing.solar.supplementary.SupplementarySolarPacket;

import java.util.Collection;
import java.util.Set;

public interface DailyFXPacket extends SupplementarySolarPacket, DailyData, DailyBatteryVoltage {

	@Override
	default boolean isNewDay(DailyData previousDailyData){
		if (!(previousDailyData instanceof DailyFXPacket)) {
			throw new IllegalArgumentException("previousDailyData is not a DailyFXPacket! It's: " + previousDailyData.getClass().getName());
		}
		DailyFXPacket previous = (DailyFXPacket) previousDailyData;
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

	int getErrorModeValue();
	default Set<FXErrorMode> getErrorModes(){ return Modes.getActiveModes(FXErrorMode.class, getErrorModeValue()); }

	int getWarningModeValue();
	default Set<WarningMode> getWarningModes(){ return Modes.getActiveModes(WarningMode.class, getWarningModeValue()); }

	int getMiscValue();
	default Set<MiscMode> getMiscModes(){ return Modes.getActiveModes(MiscMode.class, getMiscValue()); }

	Collection<Integer> getACModeValues();
	default Set<ACMode> getACModes(){ return Modes.getActiveModes(ACMode.class, getACModeValues()); }
}
