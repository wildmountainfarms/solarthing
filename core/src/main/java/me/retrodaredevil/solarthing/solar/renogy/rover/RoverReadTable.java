package me.retrodaredevil.solarthing.solar.renogy.rover;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.solar.common.*;
import me.retrodaredevil.solarthing.solar.renogy.BatteryType;
import me.retrodaredevil.solarthing.solar.renogy.ProductType;
import me.retrodaredevil.solarthing.solar.renogy.Version;
import me.retrodaredevil.solarthing.solar.renogy.Voltage;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.ImmutableSpecialPowerControl_E021;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.ImmutableSpecialPowerControl_E02D;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.SpecialPowerControl_E021;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.SpecialPowerControl_E02D;
import javax.validation.constraints.NotNull;

import java.util.Collection;

@JsonExplicit
public interface RoverReadTable extends Rover, ErrorReporter, BasicChargeController, DailyChargeController, DailyBatteryVoltage, Identifiable {

	@NotNull
	@Override
	RoverIdentifier getIdentifier();

	@Override
	default boolean isNewDay(DailyData previousDailyData) {
		if (!(previousDailyData instanceof RoverReadTable)) {
			throw new IllegalArgumentException("previousDailyData is not a RoverReadTable! It's: " + previousDailyData.getClass().getName());
		}
		RoverReadTable previous = (RoverReadTable) previousDailyData;
		return getDailyKWH() < previous.getDailyKWH() || getDailyAH() < previous.getDailyAH() ||
				getDailyMinBatteryVoltage() > previous.getDailyMinBatteryVoltage() || // the min voltage was reset to a larger number
				getDailyMaxBatteryVoltage() < previous.getDailyMaxBatteryVoltage() || // the max voltage was reset to a smaller number
				getOperatingDaysCount() > previous.getOperatingDaysCount() || // The day increased
				getDailyMaxChargingPower() < previous.getDailyMaxChargingPower(); // The max charging power was reset to a smaller number
	}

	/**
	 * Should be serialized as "maxVoltage"
	 * @return The int value of the max voltage
	 */
	@JsonProperty("maxVoltage")
	int getMaxVoltageValue();

	/** @return The enum value representing the max voltage */
	default Voltage getMaxVoltage(){ return Modes.getActiveMode(Voltage.class, getMaxVoltageValue()); }

	/**
	 * Should be serialized as "ratedChargingCurrent"
	 * @return The rated charging current
	 */
	@JsonProperty("ratedChargingCurrent")
	int getRatedChargingCurrentValue();

	/**
	 * Should be serialized as "ratedDischargingCurrent"
	 * @return The rated discharging current
	 */
	@JsonProperty("ratedDischargingCurrent")
	int getRatedDischargingCurrentValue();

	/**
	 * Should be serialized as "productType"
	 * @return The int value representing the product type
	 */
	@JsonProperty("productType")
	int getProductTypeValue();
	/**
	 * @return The enum value representing the product type
	 */
	default ProductType getProductType(){ return Modes.getActiveMode(ProductType.class, getProductTypeValue()); }

	/**
	 * If serialized in JSON, should first be converted to Base64, then stored as "productModelEncoded"
	 * @return An array of 16 bytes in length representing the product model
	 */
	@JsonProperty("productModelEncoded") // jackson should encode and decode as this as base64 when serializing and deserializing
	byte[] getProductModelValue();

	/**
	 * Should be serialized as "productModelString"
	 * @return The string representing the product model
	 */
	@JsonProperty("productModelString")
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
	@JsonProperty("softwareVersion")
	int getSoftwareVersionValue();
	/**
	 * If serialized, should be serialized as "softwareVersionString" using {@link Version#toString()}
	 * @return The {@link Version} object representing the software version
	 */
	@JsonSerialize(using = Version.StringOnlySerializer.class)
	@JsonProperty("softwareVersionString")
	default Version getSoftwareVersion(){ return new Version(getSoftwareVersionValue()); }

	/**
	 * Should be serialized as "hardwareVersion"
	 * @return The int value representing the hardware version
	 */
	@JsonProperty("hardwareVersion")
	int getHardwareVersionValue();
	/**
	 * If serialized, should be serialized as "hardwareVersionString" using {@link Version#toString()}
	 * @return The {@link Version} object representing the hardware version
	 */
	@JsonSerialize(using = Version.StringOnlySerializer.class)
	@JsonProperty("hardwareVersionString")
	default Version getHardwareVersion() { return new Version(getHardwareVersionValue()); }

	/**
	 * Should be serialized as "productSerialNumber"
	 * @return The serial number
	 */
	@JsonProperty("productSerialNumber")
	int getProductSerialNumber();

	/**
	 * Should be serialized as "controllerDeviceAddress"
	 * @return A number in range [1..247] representing the controller device address
	 */
	@JsonProperty("controllerDeviceAddress")
	int getControllerDeviceAddress();

	/**
	 * @return A number in range [0..100] representing the current battery capacity value (The battery percentage)
	 */
	@JsonProperty("batteryCapacitySOC")
	int getBatteryCapacitySOC();

	@Override
	float getBatteryVoltage();

	/**
	 * Should be serialized as "chargingCurrent"
	 * @return The charging current
	 */
	@JsonProperty("chargingCurrent")
	@Override
	@NotNull
	Float getChargingCurrent();

	/**
	 * Should be serialized as "controllerTemperatureRaw"
	 * @return The raw controller temperature
	 */
	@JsonProperty("controllerTemperatureRaw")
	int getControllerTemperatureRaw();
	/**
	 * Should be serialized as "batteryTemperatureRaw"
	 * @return The raw battery temperature
	 */
	@JsonProperty("batteryTemperatureRaw")
	int getBatteryTemperatureRaw();

	/**
	 * @return The temperature of the controller in degrees celsius
	 */
	@GraphQLInclude("controllerTemperatureCelsius")
	default int getControllerTemperatureCelsius(){
		return convertRawTemperature(getControllerTemperatureRaw());
	}
	/**
	 * @return The temperature of the battery in degrees celsius
	 */
	@GraphQLInclude("batteryTemperatureCelsius")
	default int getBatteryTemperatureCelsius(){
		return convertRawTemperature(getBatteryTemperatureRaw());
	}
	static int convertRawTemperature(int temperatureRaw){
		if(temperatureRaw > 127){
			return 128 - temperatureRaw; // uses bit 7 (8th bit) as a sign indicator. Signed numbers are not represented in the standard way
		}
		return temperatureRaw;
	}
	@Deprecated
	default int getControllerTemperature(){ return getControllerTemperatureCelsius(); }
	@Deprecated
	default int getBatteryTemperature(){ return getBatteryTemperatureCelsius(); }
	@GraphQLInclude("controllerTemperatureFahrenheit")
	default float getControllerTemperatureFahrenheit(){
		return getControllerTemperatureCelsius() * 9 / 5.0f + 32;
	}
	@GraphQLInclude("batteryTemperatureFahrenheit")
	default float getBatteryTemperatureFahrenheit(){
		return getBatteryTemperatureCelsius() * 9 / 5.0f + 32;
	}

	/**
	 * Should be serialized as "loadVoltage"
	 * <p>
	 * AKA street light voltage
	 * @return The load/street light voltage in volts
	 */
	@JsonProperty("loadVoltage")
	float getLoadVoltage();

	/**
	 * Should be serialized as "loadCurrent"
	 * <p>
	 * AKA street light current
	 * @return The load/street light current in amps
	 */
	@JsonProperty("loadCurrent")
	float getLoadCurrent();

	/**
	 * Should be serialized as "loadPower"
	 * <p>
	 * AKA street light power
	 * @return The load/street light power in watts
	 */
	@JsonProperty("loadPower")
	int getLoadPower();

	/** AKA PV/Solar Panel voltage*/
	@NotNull
    @JsonProperty("inputVoltage")
	@Override
	Float getInputVoltage();
	@NotNull
	@JsonProperty("pvCurrent")
	@Override
	Float getPVCurrent();
	@JsonProperty("chargingPower")
	@Override
	@NotNull
	Integer getChargingPower();

	@Override
	float getDailyMinBatteryVoltage();
	@Override
	float getDailyMaxBatteryVoltage();

	/**
	 * @return The daily record for the maximum charger current
	 */
	@JsonProperty("dailyMaxChargingCurrent")
	float getDailyMaxChargingCurrent();

	/**
	 * @return The daily record for the maximum discharging current
	 */
	@JsonProperty("dailyMaxDischargingCurrent")
	float getDailyMaxDischargingCurrent();
	@JsonProperty("dailyMaxChargingPower")
	int getDailyMaxChargingPower();
	@JsonProperty("dailyMaxDischargingPower")
	int getDailyMaxDischargingPower();

	@JsonProperty("dailyAH")
	@Override
	int getDailyAH();
	@JsonProperty("dailyAHDischarging")
	int getDailyAHDischarging();

	// 0x0113
	@Override
	float getDailyKWH();
	@JsonProperty("dailyKWHConsumption")
	float getDailyKWHConsumption();

	@JsonProperty("operatingDaysCount")
	int getOperatingDaysCount();
	@JsonProperty("batteryOverDischargesCount")
	int getBatteryOverDischargesCount();
	@JsonProperty("batteryFullChargesCount")
	int getBatteryFullChargesCount();
	@JsonProperty("chargingAmpHoursOfBatteryCount")
	int getChargingAmpHoursOfBatteryCount();
	@JsonProperty("dischargingAmpHoursOfBatteryCount")
	int getDischargingAmpHoursOfBatteryCount();
	@JsonProperty("cumulativeKWH")
	float getCumulativeKWH();
	@JsonProperty("cumulativeKWHConsumption")
	float getCumulativeKWHConsumption();

	@JsonProperty("streetLightValue")
	int getRawStreetLightValue();
	default int getStreetLightStatusValue(){
		return ~StreetLight.IGNORED_BITS & getRawStreetLightValue();
	}
	default StreetLight getStreetLightStatus(){ return Modes.getActiveMode(StreetLight.class, getStreetLightStatusValue()); }
	@JsonProperty("streetLightBrightness") // convenient
	default int getStreetLightBrightnessPercent(){ return StreetLight.getBrightnessValue(getRawStreetLightValue()); }
	@JsonProperty("streetLightOn") // convenient
	default boolean isStreetLightOn(){ return getStreetLightStatus() == StreetLight.ON; }

	@JsonProperty("chargingState")
	int getChargingStateValue();
	@Override
	default @NotNull ChargingState getChargingMode(){ return Modes.getActiveMode(ChargingState.class, getChargingStateValue()); }
	@JsonProperty("chargingStateName") // convenient
	default String getChargingStateName(){ return getChargingMode().getModeName(); }

	@JsonProperty("errorMode")
	@Override
	int getErrorModeValue();
	@Override
	default Collection<RoverErrorMode> getErrorModes(){
		return Modes.getActiveModes(RoverErrorMode.class, getErrorModeValue());
	}
	@JsonProperty("errors")
	default String getErrorsString(){
		return Modes.toString(RoverErrorMode.class, getErrorModeValue());
	}

	/**
	 *
	 * @return The nominal battery capacity in AmpHours (AH)
	 */
	@JsonProperty("nominalBatteryCapacity")
	int getNominalBatteryCapacity();

	/** Should be serialized as "systemVoltageSetting" */
	@JsonProperty("systemVoltageSetting")
	int getSystemVoltageSettingValue();
	/** Should be serialized as "recognizedVoltage" */
	@JsonProperty("recognizedVoltage")
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
	@JsonProperty("batteryType")
	int getBatteryTypeValue();
	default BatteryType getBatteryType(){ return Modes.getActiveMode(BatteryType.class, getBatteryTypeValue()); }
	@JsonProperty("batteryTypeName") // convenient
	default String getBatteryTypeName(){ return getBatteryType().getModeName(); }

	/** Called "High Voltage Disconnect" */
	@JsonProperty("overVoltageThresholdRaw")
	int getOverVoltageThresholdRaw();
	@JsonProperty("chargingVoltageLimitRaw")
	int getChargingVoltageLimitRaw();
	@JsonProperty("equalizingChargingVoltageRaw")
	int getEqualizingChargingVoltageRaw();
	/** AKA overcharge voltage (for lithium batteries) */
	@JsonProperty("boostChargingVoltageRaw")
	int getBoostChargingVoltageRaw();
	/** AKA overcharge recovery voltage (for lithium batteries) */
	@JsonProperty("floatingChargingVoltageRaw")
	int getFloatingChargingVoltageRaw();
	@JsonProperty("boostChargingRecoveryVoltageRaw")
	int getBoostChargingRecoveryVoltageRaw();
	/** Called "Low Voltage Reconnect"*/
	@JsonProperty("overDischargeRecoveryVoltageRaw")
	int getOverDischargeRecoveryVoltageRaw();
	@JsonProperty("underVoltageWarningLevelRaw")
	int getUnderVoltageWarningLevelRaw();
	/** Called "Low Voltage Disconnect" */
	@JsonProperty("overDischargeVoltageRaw")
	int getOverDischargeVoltageRaw();
	@JsonProperty("dischargingLimitVoltageRaw")
	int getDischargingLimitVoltageRaw();

	// 0xE00F
	@JsonProperty("endOfChargeSOC")
	int getEndOfChargeSOC();
	@JsonProperty("endOfDischargeSOC")
	int getEndOfDischargeSOC();

	@JsonProperty("overDischargeTimeDelaySeconds")
	int getOverDischargeTimeDelaySeconds();

	@Deprecated
	static int getEqualizingChargingTimeMinutesFromRaw(int raw){ return raw + 10; }
	@JsonProperty("equalizingChargingTimeRaw")
	int getEqualizingChargingTimeRaw();
	default int getEqualizingChargingTimeMinutes(){ return getEqualizingChargingTimeRaw(); }

	@Deprecated
	static int getBoostChargingTimeMinutesFromRaw(int raw){ return raw + 10; }
	@JsonProperty("boostChargingTimeRaw")
	int getBoostChargingTimeRaw();
	default int getBoostChargingTimeMinutes(){ return getBoostChargingTimeRaw(); }

	@Deprecated
	static int getEqualizingChargingIntervalDaysFromRaw(int raw){
		if(raw == 0){
			return 0;
		}
		return raw + 5;
	}
	@JsonProperty("equalizingChargingIntervalRaw")
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
	@JsonProperty("temperatureCompensationFactorRaw")
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
	@JsonProperty("operatingStage1")
	default OperatingSettingBundle getOperatingStage1(){ return getOperatingSettingBundle(OperatingSetting.STAGE_1); }
	@JsonProperty("operatingStage2")
	default OperatingSettingBundle getOperatingStage2(){ return getOperatingSettingBundle(OperatingSetting.STAGE_2); }
	@JsonProperty("operatingStage3")
	default OperatingSettingBundle getOperatingStage3(){ return getOperatingSettingBundle(OperatingSetting.STAGE_3); }
	@JsonProperty("operatingMorningOn")
	default OperatingSettingBundle getOperatingMorningOn(){ return getOperatingSettingBundle(OperatingSetting.MORNING_ON); }

	/** Should be serialized as "loadWorkingMode" */
	@JsonProperty("loadWorkingMode")
	int getLoadWorkingModeValue();
	default LoadWorkingMode getLoadWorkingMode() { return Modes.getActiveMode(LoadWorkingMode.class, getLoadWorkingModeValue()); }
	@JsonProperty("loadWorkingModeName") // convenient string
	default String getLoadWorkingModeName() { return getLoadWorkingMode().getModeName(); }

	@JsonProperty("lightControlDelayMinutes")
	int getLightControlDelayMinutes();
	@JsonProperty("lightControlVoltage")
	int getLightControlVoltage();

	// 0xE020
	@JsonProperty("ledLoadCurrentSettingRaw")
	int getLEDLoadCurrentSettingRaw();
	/** Units: mA */
	default int getLEDLoadCurrentSettingMilliAmps(){ return getLEDLoadCurrentSettingRaw() * 10; }

	@JsonProperty("specialPowerControlE021Raw")
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
	@JsonProperty("sensed1")
	default SensingBundle getSensed1(){ return getSensingBundle(Sensing.SENSING_1); }
	@JsonProperty("sensed2")
	default SensingBundle getSensed2(){ return getSensingBundle(Sensing.SENSING_2); }
	@JsonProperty("sensed3")
	default SensingBundle getSensed3(){ return getSensingBundle(Sensing.SENSING_3); }


	@JsonProperty("sensingTimeDelayRaw")
	int getSensingTimeDelayRaw();
	default int getSensingTimeDelaySeconds(){ return getSensingTimeDelayRaw() + 10; }

	@JsonProperty("ledLoadCurrentRaw")
	int getLEDLoadCurrentRaw();
	/** Units: mA */
	default int getLEDLoadCurrentMilliAmps(){ return getLEDLoadCurrentRaw() * 10; }

	@JsonProperty("specialPowerControlE02DRaw")
	int getSpecialPowerControlE02DRaw();
	default SpecialPowerControl_E02D getSpecialPowerControlE02D(){ return new ImmutableSpecialPowerControl_E02D(getSpecialPowerControlE02DRaw()); }

}
