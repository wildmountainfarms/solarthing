package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;
import me.retrodaredevil.solarthing.solar.common.ChargeController;
import me.retrodaredevil.solarthing.solar.common.DailyData;
import me.retrodaredevil.solarthing.solar.renogy.BatteryType;
import me.retrodaredevil.solarthing.solar.renogy.ProductType;
import me.retrodaredevil.solarthing.solar.renogy.Version;
import me.retrodaredevil.solarthing.solar.renogy.Voltage;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.ImmutableSpecialPowerControl_E021;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.ImmutableSpecialPowerControl_E02D;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.SpecialPowerControl_E021;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.SpecialPowerControl_E02D;

@SuppressWarnings("unused")
public interface RoverReadTable extends Rover, ChargeController, DailyData {
	int getMaxVoltageValue();
	default Voltage getMaxVoltage(){ return Modes.getActiveMode(Voltage.class, getMaxVoltageValue()); }
	
	int getRatedChargingCurrentValue();
	default RatedCurrent getRatedChargingCurrent(){ return Modes.getActiveMode(RatedCurrent.class, getRatedChargingCurrentValue()); }
	
	int getRatedDischargingCurrentValue();
	default RatedCurrent getRatedDischargingCurrent(){ return Modes.getActiveMode(RatedCurrent.class, getRatedDischargingCurrentValue()); }
	
	int getProductTypeValue();
	default ProductType getProductType(){ return Modes.getActiveMode(ProductType.class, getProductTypeValue()); }
	
	/**
	 * @return An array of 16 bytes in length representing the product model
	 */
	byte[] getProductModelValue(); // TODO maybe change this signature
	default String getProductModel(){
		byte[] raw = getProductModelValue();
		if(raw.length != 16){
			throw new IllegalStateException();
		}
		StringBuilder r = new StringBuilder(15);
		for(byte value : raw){
			int intValue = ((int) value) & 0xff;
			if(intValue < 0) throw new AssertionError();
			if(intValue > 127) throw new IllegalStateException(intValue + " is out of normal ascii range!");
			if(intValue == 0x20) continue; // space
			// TODO check if there are other values we need to ignore
			r.append((char) value);
		}
		return r.toString();
	}
	int getSoftwareVersionValue();
	default Version getSoftwareVersion(){ return new Version(getSoftwareVersionValue()); }
	int getHardwareVersionValue();
	default Version getHardwareVersion() { return new Version(getHardwareVersionValue()); }
	int getProductSerialNumber();
	
	/**
	 * Should be serialized as "address"
	 * @return A number in range [1..247] representing the controller device address
	 */
	int getControllerDeviceAddress();
	
	/**
	 * @return A number in range [0..100] representing the current battery capacity value
	 */
	int getBatteryCapacitySOC();
	
	// implements BatteryVoltage
	
	@Override
	Float getChargerCurrent();
	
	int getControllerTemperature();
	int getBatteryTemperature();
	
	/** AKA street light voltage*/
	float getLoadVoltage();
	float getLoadCurrent();
	int getLoadPower();
	
	/** AKA PV/Solar Panel voltage*/
	@Override
	Float getInputVoltage();
	@Override
	Float getPVCurrent();
	@Override
	Integer getChargingPower();
	
	BatteryVoltage getDailyMinBatteryVoltage();
	BatteryVoltage getDailyMaxBatteryVoltage();
	
	/**
	 * @return The daily record for the maximum charger current
	 */
	float getDailyMaxChargerCurrent();
	
	/**
	 * @return The daily record for the maximum discharging current
	 */
	float getDailyMaxDischargingCurrent();
	int getDailyMaxChargingPower();
	int getDailyMaxDischargingPower();
	
	@Override
	int getDailyAH();
	int getDailyAHDischarging();
	
	// 0x0113
	@Override
	float getDailyKWH();
	float getDailyKWHConsumption();
	
	int getOperatingDaysCount();
	int getBatteryOverDischargesCount();
	int getBatteryFullChargesCount();
	int getChargingAmpHoursOfBatteryCount();
	int getDischargingAmpHoursOfBatteryCount();
	float getCumulativeKWH();
	float getCumulativeKWHConsumption();
	
	int getStreetLightValue();
	default StreetLight getStreetLightStatus(){ return Modes.getActiveMode(StreetLight.class, getStreetLightValue()); }
	default int getStreetLightBrightnessPercent(){ return StreetLight.getBrightnessValue(getStreetLightValue()); }
	
	int getChargingStateValue();
	default ChargingState getChargingState(){ return Modes.getActiveMode(ChargingState.class, getChargingStateValue()); }
	
	int getErrorMode();
	
	/**
	 *
	 * @return The nominal battery capacity in AmpHours (AH)
	 */
	int getNominalBatteryCapacity();
	
	/** Should be serialized as "systemVoltageSetting" */
	int getSystemVoltageSettingValue();
	/** Should be serialized as "recognizedVoltage" */
	int getRecognizedVoltageValue();
	default SpecialPowerControl_E02D.SystemVoltage getSystemVoltageSetting(){ return Modes.getActiveMode(SpecialPowerControl_E02D.SystemVoltage.class, getSystemVoltageSettingValue()); }
	default SpecialPowerControl_E02D.SystemVoltage getRecognizedVoltage(){ return Modes.getActiveMode(SpecialPowerControl_E02D.SystemVoltage.class, getRecognizedVoltageValue()); }
	
	// 0xE004
	/** Should be serialized as "batteryType" */
	int getBatteryTypeValue();
	default BatteryType getBatteryType(){ return Modes.getActiveMode(BatteryType.class, getBatteryTypeValue()); }
	
	int getOverVoltageThresholdRaw(); // TODO add non-raw method that returns a float
	int getChargingVoltageLimitRaw();
	int getEqualizingChargingVoltageRaw();
	/** AKA overcharge voltage (for lithium batteries) */
	int getBoostChargingVoltageRaw();
	/** AKA overcharge recovery voltage (for lithium batteries) */
	int getFloatingChargingVoltageRaw();
	int getBoostChargingRecoveryVoltageRaw();
	int getOverDischargeRecoveryVoltageRaw();
	int getUnderVoltageWarningLevelRaw();
	int getOverDischargeVoltageRaw();
	int getDischargingLimitVoltageRaw();
	
	// 0xE00F
	int getEndOfChargeSOC(); // TODO figure out units and what this means
	int getEndOfDischargeSOC();
	
	int getOverDischargeTimeDelaySeconds();
	
	int getEqualizingChargingTimeRaw();
	default int getEqualizingChargingTimeMinutes(){ return getEqualizingChargingTimeRaw() + 10; }
	
	int getBoostChargingTimeRaw();
	default int getBoostChargingTimeMinutes(){ return getBoostChargingTimeRaw() + 10; }
	
	int getEqualizingChargingIntervalRaw();
	default int getEqualizingChargingIntervalDays(){
		int raw = getEqualizingChargingIntervalRaw();
		if(raw == 0){
			return 0;
		}
		return raw + 5;
	}
	
	int getTemperatureCompensationFactorRaw();
	/** Units: mV/C/2V*/
	default int getTemperatureCompensationFactor(){
		int raw = getTemperatureCompensationFactorRaw();
		if(raw == 0){
			return 0;
		}
		return raw + 1;
	}
	
	//0xE015
	int getDurationHours(OperatingSetting setting);
	int getOperatingPowerPercentage(OperatingSetting setting);
	
	/** Should be serialized as "loadWorkingMode" */
	int getLoadWorkingModeValue();
	default LoadWorkingMode getLoadWorkingMode() { return Modes.getActiveMode(LoadWorkingMode.class, getLoadWorkingModeValue()); }
	
	int getLightControlDelayMinutes();
	int getLightControlVoltage();
	
	// 0xE020
	int getLEDLoadCurrentSettingRaw();
	/** Units: mA */
	default int getLEDLoadCurrentSettingMilliAmps(){ return getLEDLoadCurrentSettingRaw() * 10; }
	
	int getSpecialPowerControlE021Raw();
	default SpecialPowerControl_E021 getSpecialPowerControlE021(){ return new ImmutableSpecialPowerControl_E021(getSpecialPowerControlE021Raw()); }
	
	int getWorkingHoursRaw(PowerSensing powerSensing); // TODO add setters in RoverWriteTable
	default int getWorkingHours(PowerSensing powerSensing){ return getWorkingHoursRaw(powerSensing) + 1; }
	int getPowerWithPeopleSensedRaw(PowerSensing powerSensing);
	default int getPowerWithPeopleSensed(PowerSensing powerSensing){ return getPowerWithPeopleSensedRaw(powerSensing) + 10; }
	int getPowerWithNoPeopleSensedRaw(PowerSensing powerSensing);
	default int getPowerWithNoPeopleSensed(PowerSensing powerSensing){ return getPowerWithNoPeopleSensedRaw(powerSensing) + 10; }
	
	
	int getSensingTimeDelayRaw();
	default int getSensingTimeDelaySeconds(){ return getSensingTimeDelayRaw() + 10; }
	
	int getLEDLoadCurrentRaw();
	/** Units: mA */
	default int getLEDLoadCurrentMilliAmps(){ return getLEDLoadCurrentRaw() * 10; }
	
	int getSpecialPowerControlE02DRaw();
	default SpecialPowerControl_E02D getSpecialPowerControlE0D1(){ return new ImmutableSpecialPowerControl_E02D(getSpecialPowerControlE02DRaw()); }
	
}
