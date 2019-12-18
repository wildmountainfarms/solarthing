package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.solar.common.ChargeController;
import me.retrodaredevil.solarthing.solar.common.DailyBatteryVoltage;
import me.retrodaredevil.solarthing.solar.common.DailyData;
import me.retrodaredevil.solarthing.solar.common.ErrorReporter;
import me.retrodaredevil.solarthing.solar.renogy.*;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.ImmutableSpecialPowerControl_E021;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.ImmutableSpecialPowerControl_E02D;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.SpecialPowerControl_E021;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.SpecialPowerControl_E02D;

import java.util.Collection;

@SuppressWarnings("unused")
public interface RoverReadTable extends Rover, ErrorReporter, ChargeController, DailyData, DailyBatteryVoltage, Identifiable {
	
	@Override
	RoverIdentifier getIdentifier();
	
	@Override
	default boolean isNewDay(DailyData previousDailyData) {
		if(DailyData.super.isNewDay(previousDailyData)){
			return true;
		}
		if (!(previousDailyData instanceof RoverReadTable)) {
			throw new IllegalArgumentException("previousDailyData is not a RoverReadTable! It's: " + previousDailyData.getClass().getName());
		}
		RoverReadTable previous = (RoverReadTable) previousDailyData;
		return getDailyMinBatteryVoltage() > previous.getDailyMinBatteryVoltage() || // the min voltage was reset to a larger number
			getDailyMaxBatteryVoltage() < previous.getDailyMaxBatteryVoltage() || // the max voltage was reset to a smaller number
			getOperatingDaysCount() > previous.getOperatingDaysCount() || // The
			getDailyMaxChargingPower() < previous.getDailyMaxChargingPower(); // The max charging power was reset to a smaller number
	}
	
	/**
	 * Should be serialized as "maxVoltage"
	 * @return The int value of the max voltage
	 */
	int getMaxVoltageValue();
	
	/** @return The enum value representing the max voltage */
	default Voltage getMaxVoltage(){ return Modes.getActiveMode(Voltage.class, getMaxVoltageValue()); }
	
	/**
	 * Should be serialized as "ratedChargingCurrent"
	 * @return The rated charging current
	 */
	int getRatedChargingCurrentValue();

	/**
	 * Should be serialized as "ratedDischargingCurrent"
	 * @return The rated discharging current
	 */
	int getRatedDischargingCurrentValue();

	/**
	 * Should be serialized as "productType"
	 * @return The int value representing the product type
	 */
	int getProductTypeValue();
	/**
	 * @return The enum value representing the product type
	 */
	default ProductType getProductType(){ return Modes.getActiveMode(ProductType.class, getProductTypeValue()); }
	
	/**
	 * If serialized in JSON, should first be converted to Base64, then stored as "productModelEncoded"
	 * @return An array of 16 bytes in length representing the product model
	 */
	byte[] getProductModelValue();
	
	/**
	 * Should be serialized as "productModelString"
	 * @return The string representing the product model
	 */
	default String getProductModel(){
		byte[] raw = getProductModelValue();
		if(raw.length != 16){
			throw new IllegalStateException();
		}
		StringBuilder r = new StringBuilder();
		for(byte value : raw){
			int intValue = ((int) value) & 0xff;
			if(intValue > 127) throw new IllegalStateException(intValue + " is out of normal ascii range!");
			if(intValue == 0x20) continue; // space // really, we don't have to do this, but the documentation says we should
			r.append((char) value);
		}
		return r.toString();
	}
	
	/**
	 * Should be serialized as "softwareVersion"
	 * @return The int value representing the software version
	 */
	int getSoftwareVersionValue();
	/**
	 * If serialized, should be serialized as "softwareVersionString" using {@link Version#toString()}
	 * @return The {@link Version} object representing the software version
	 */
	default Version getSoftwareVersion(){ return new Version(getSoftwareVersionValue()); }
	
	/**
	 * Should be serialized as "hardwareVersion"
	 * @return The int value representing the hardware version
	 */
	int getHardwareVersionValue();
	/**
	 * If serialized, should be serialized as "hardwareVersionString" using {@link Version#toString()}
	 * @return The {@link Version} object representing the hardware version
	 */
	default Version getHardwareVersion() { return new Version(getHardwareVersionValue()); }
	
	/**
	 * Should be serialized as "productSerialNumber"
	 * @return The serial number
	 */
	int getProductSerialNumber();
	
	/**
	 * Should be serialized as "address"
	 * @return A number in range [1..247] representing the controller device address
	 */
	int getControllerDeviceAddress();
	
	/**
	 * @return A number in range [0..100] representing the current battery capacity value (The battery percentage)
	 */
	int getBatteryCapacitySOC();
	
	@Override
	float getBatteryVoltage();
	
	/**
	 * Should be serialized as "chargingCurrent"
	 * @return The charging current
	 */
	@Override
	Float getChargingCurrent();
	
	/**
	 * Should be serialized as "controllerTemperatureRaw"
	 * @return The raw controller temperature
	 */
	int getControllerTemperatureRaw();
	/**
	 * Should be serialized as "batteryTemperatureRaw"
	 * @return The raw battery temperature
	 */
	int getBatteryTemperatureRaw();
	
	/**
	 * @return The temperature of the controller in degrees celsius
	 */
	default int getControllerTemperature(){
		return convertRawTemperature(getControllerTemperatureRaw());
	}
	/**
	 * @return The temperature of the battery in degrees celsius
	 */
	default int getBatteryTemperature(){
		return convertRawTemperature(getBatteryTemperatureRaw());
	}
	static int convertRawTemperature(int temperatureRaw){
		if(temperatureRaw > 127){
			return 128 - temperatureRaw; // uses bit 7 (8th bit) as a sign indicator. Signed numbers are not represented in the standard way
		}
		return temperatureRaw;
	}
	
	/**
	 * Should be serialized as "loadVoltage"
	 * <p>
	 * AKA street light voltage
	 * @return The load/street light voltage in volts
	 */
	float getLoadVoltage();
	
	/**
	 * Should be serialized as "loadCurrent"
	 * <p>
	 * AKA street light current
	 * @return The load/street light current in amps
	 */
	float getLoadCurrent();
	
	/**
	 * Should be serialized as "loadPower"
	 * <p>
	 * AKA street light power
	 * @return The load/street light power in watts
	 */
	int getLoadPower();
	
	/** AKA PV/Solar Panel voltage*/
	@Override
	Float getInputVoltage();
	@Override
	Float getPVCurrent();
	@Override
	Integer getChargingPower();

	@Override
	float getDailyMinBatteryVoltage();
	@Override
	float getDailyMaxBatteryVoltage();
	
	/**
	 * @return The daily record for the maximum charger current
	 */
	float getDailyMaxChargingCurrent();
	
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
	@Override
	default ChargingState getChargingMode(){ return Modes.getActiveMode(ChargingState.class, getChargingStateValue()); }
	
	@Override
	int getErrorMode();
	@Override
	default Collection<RoverErrorMode> getActiveErrors(){
		return Modes.getActiveModes(RoverErrorMode.class, getErrorMode());
	}
	
	/**
	 *
	 * @return The nominal battery capacity in AmpHours (AH)
	 */
	int getNominalBatteryCapacity();
	
	/** Should be serialized as "systemVoltageSetting" */
	int getSystemVoltageSettingValue();
	/** Should be serialized as "recognizedVoltage" */
	int getRecognizedVoltageValue();
	
	/** @return Any voltage from {@link Voltage} representing the current voltage setting */
	default Voltage getSystemVoltageSetting(){ return Modes.getActiveMode(Voltage.class, getSystemVoltageSettingValue(), Voltage.AUTO); }
	/**
	 * NOTE: On some products, this is broken. If this returns a {@link Voltage#AUTO} or null, you can assume it's not working correctly
	 * @return The recognized {@link Voltage} or null.
	 */
	default Voltage getRecognizedVoltage(){ return Modes.getActiveModeOrNull(Voltage.class, getRecognizedVoltageValue()); }
	
	// 0xE004
	/** Should be serialized as "batteryType" */
	int getBatteryTypeValue();
	default BatteryType getBatteryType(){ return Modes.getActiveMode(BatteryType.class, getBatteryTypeValue()); }
	
	int getOverVoltageThresholdRaw();
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
	int getEndOfChargeSOC();
	int getEndOfDischargeSOC();
	
	int getOverDischargeTimeDelaySeconds();
	
	@Deprecated
	static int getEqualizingChargingTimeMinutesFromRaw(int raw){ return raw + 10; }
	int getEqualizingChargingTimeRaw();
	default int getEqualizingChargingTimeMinutes(){ return getEqualizingChargingTimeRaw(); }
	
	@Deprecated
	static int getBoostChargingTimeMinutesFromRaw(int raw){ return raw + 10; }
	int getBoostChargingTimeRaw();
	default int getBoostChargingTimeMinutes(){ return getBoostChargingTimeRaw(); }
	
	@Deprecated
	static int getEqualizingChargingIntervalDaysFromRaw(int raw){
		if(raw == 0){
			return 0;
		}
		return raw + 5;
	}
	int getEqualizingChargingIntervalRaw();
	default int getEqualizingChargingIntervalDays(){
		return getEqualizingChargingIntervalRaw();
	}
	
	@Deprecated
	static int getTemperatureCompensationFactorFromRaw(int raw){
		if(raw == 0){
			return 0;
		}
		return raw + 1;
	}
	int getTemperatureCompensationFactorRaw();
	/** Units: mV/C/2V*/
	default int getTemperatureCompensationFactor(){
		return getTemperatureCompensationFactorRaw();
	}
	
	//0xE015
	int getOperatingDurationHours(OperatingSetting setting);
	int getOperatingPowerPercentage(OperatingSetting setting);
	default OperatingSettingBundle getOperatingSettingBundle(OperatingSetting setting){
		return new OperatingSettingBundle(getOperatingDurationHours(setting), getOperatingPowerPercentage(setting));
	}
	
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
	
	int getWorkingHoursRaw(Sensing sensing);
	default int getWorkingHours(Sensing sensing){ return getWorkingHoursRaw(sensing) + 1; }
	int getPowerWithPeopleSensedRaw(Sensing sensing);
	default int getPowerWithPeopleSensedPercentage(Sensing sensing){ return getPowerWithPeopleSensedRaw(sensing) + 10; }
	int getPowerWithNoPeopleSensedRaw(Sensing sensing);
	default int getPowerWithNoPeopleSensedPercentage(Sensing sensing){ return getPowerWithNoPeopleSensedRaw(sensing) + 10; }
	default SensingBundle getSensingBundle(Sensing sensing){
		return new SensingBundle(getWorkingHoursRaw(sensing), getPowerWithPeopleSensedRaw(sensing), getPowerWithNoPeopleSensedRaw(sensing));
	}
	
	
	int getSensingTimeDelayRaw();
	default int getSensingTimeDelaySeconds(){ return getSensingTimeDelayRaw() + 10; }
	
	int getLEDLoadCurrentRaw();
	/** Units: mA */
	default int getLEDLoadCurrentMilliAmps(){ return getLEDLoadCurrentRaw() * 10; }
	
	int getSpecialPowerControlE02DRaw();
	default SpecialPowerControl_E02D getSpecialPowerControlE02D(){ return new ImmutableSpecialPowerControl_E02D(getSpecialPowerControlE02DRaw()); }
	
}
