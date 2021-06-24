package me.retrodaredevil.solarthing.solar.renogy.rover;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.io.serial.SerialConfigBuilder;
import me.retrodaredevil.solarthing.annotations.*;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.packets.annotations.ValidSinceVersion;
import me.retrodaredevil.solarthing.solar.common.*;
import me.retrodaredevil.solarthing.solar.renogy.*;
import me.retrodaredevil.solarthing.solar.renogy.rover.annotations.DcdcOnly;
import me.retrodaredevil.solarthing.solar.renogy.rover.annotations.ResetEvening;
import me.retrodaredevil.solarthing.solar.renogy.rover.annotations.ResetMorning;
import me.retrodaredevil.solarthing.solar.renogy.rover.annotations.RoverOnly;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.ImmutableSpecialPowerControl_E021;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.ImmutableSpecialPowerControl_E02D;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.SpecialPowerControl_E021;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.SpecialPowerControl_E02D;

import java.util.Collection;
import java.util.Collections;

import static java.util.Objects.requireNonNull;

@JsonExplicit
public interface RoverReadTable extends Rover, ErrorReporter, BasicChargeController, DailyChargeController, AdvancedAccumulatedChargeController, RecordBatteryVoltage, DualTemperature {
	SerialConfig SERIAL_CONFIG = new SerialConfigBuilder(9600)
			.setDataBits(8)
			.setParity(SerialConfig.Parity.NONE)
			.setStopBits(SerialConfig.StopBits.ONE)
			.build();

	/*
	Note, some methods in this class are annotated with @WillBeUsedEventually, which means that they do not appear in the status
	packet because of lack of testing.
	 */

	@Override
	default boolean isNewDay(DailyData previousDailyData) {
		if (!(previousDailyData instanceof RoverReadTable)) {
			throw new IllegalArgumentException("previousDailyData is not a RoverReadTable! It's: " + previousDailyData.getClass().getName());
		}
		RoverReadTable previous = (RoverReadTable) previousDailyData;
		/*
		NOTE: Some values reset at different times.
		 */
		return getDailyKWH() < previous.getDailyKWH() || getDailyAH() < previous.getDailyAH() ||
//				getDailyMinBatteryVoltage() > previous.getDailyMinBatteryVoltage() || // the min voltage was reset to a larger number
				getDailyMaxBatteryVoltage() < previous.getDailyMaxBatteryVoltage() || // the max voltage was reset to a smaller number
//				getOperatingDaysCount() > previous.getOperatingDaysCount() || // The day increased
				getDailyMaxChargingCurrent() < previous.getDailyMaxChargingCurrent() ||
				getDailyMaxChargingPower() < previous.getDailyMaxChargingPower(); // The max charging power was reset to a smaller number
	}

	@GraphQLInclude("hasLoad")
	default boolean hasLoad() {
		return getRatedDischargingCurrentValue() > 0;
	}

	/**
	 *
	 * @return true if this charge controller supports input for a DC generator(alternator).
	 */
	@GraphQLInclude("isDcdc")
	default boolean isDcdc() {
		return !hasLoad() && getMaxVoltage() == Voltage.V12 && ProductModelUtil.isDcdc(getProductModel());
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
	@ValidSinceVersion(version = RoverStatusPacket.VERSION_CORRECT_TWO_REGISTER)
	@GraphQLInclude("softwareVersionValue")
	@JsonProperty("softwareVersion")
	int getSoftwareVersionValue();
	/**
	 * If serialized, should be serialized as "softwareVersionString" using {@link Version#toString()}
	 * @return The {@link Version} object representing the software version
	 */
	@ValidSinceVersion(version = RoverStatusPacket.VERSION_CORRECT_TWO_REGISTER)
	@GraphQLInclude("softwareVersion")
	default Version getSoftwareVersion(){ return new Version(getSoftwareVersionValue()); }
	@ValidSinceVersion(version = RoverStatusPacket.VERSION_CORRECT_TWO_REGISTER)
	@JsonProperty("softwareVersionString")
	default String getSoftwareVersionString() { return getSoftwareVersion().toString(); }

	/**
	 * Should be serialized as "hardwareVersion"
	 * @return The int value representing the hardware version
	 */
	@ValidSinceVersion(version = RoverStatusPacket.VERSION_CORRECT_TWO_REGISTER)
	@GraphQLInclude("hardwareVersionValue")
	@JsonProperty("hardwareVersion")
	int getHardwareVersionValue();
	/**
	 * If serialized, should be serialized as "hardwareVersionString" using {@link Version#toString()}
	 * @return The {@link Version} object representing the hardware version
	 */
	@ValidSinceVersion(version = RoverStatusPacket.VERSION_CORRECT_TWO_REGISTER)
	@GraphQLInclude("hardwareVersion")
	default Version getHardwareVersion() { return new Version(getHardwareVersionValue()); }
	@ValidSinceVersion(version = RoverStatusPacket.VERSION_CORRECT_TWO_REGISTER)
	@JsonProperty("hardwareVersionString")
	default String getHardwareVersionString() { return getHardwareVersion().toString(); }

	/**
	 * Should be serialized as "productSerialNumber"
	 * @return The serial number
	 */
	@ValidSinceVersion(version = RoverStatusPacket.VERSION_CORRECT_TWO_REGISTER)
	@JsonProperty("productSerialNumber")
	@JsonPropertyDescription("The product serial number. Note that is not always unique as devices' serial numbers can accidentally be reset.")
	int getProductSerialNumber();

	/**
	 * Should be serialized as "controllerDeviceAddress"
	 * @return A number in range [1..247] representing the controller device address
	 */
	@JsonProperty("controllerDeviceAddress")
	@JsonPropertyDescription("The modbus address of the device")
	int getControllerDeviceAddress();

	/**
	 * On my rover 40A, got value of 309639, then 96644, then 15874497
	 * @return An int or null representing the protocol version
	 */
	@DcdcOnly
	@WillBeUsedEventually
	default @Nullable Integer getProtocolVersionValue() { return null; }

	/**
	 * NOTE: The default identification code is -1, or unsigned it is 0xFFFFFFFF. It is likely
	 * that if this is not -1, you should express its value as an unsigned integer.
	 *
	 * On my rover 40A, got value of 309639
	 *
	 * @return An int or null representing the unique identification code.
	 */
	@DcdcOnly
	@WillBeUsedEventually
	default @Nullable Integer getUniqueIdentificationCode() { return null; }

	// ===============================

	/**
	 * @return A number in range [0..100] representing the current battery capacity value (The battery percentage)
	 */
	@JsonProperty("batteryCapacitySOC")
	@JsonPropertyDescription("The state of charge of the battery. (A number from 0 to 100). Note this is not usually accurate.")
	int getBatteryCapacitySOC();

	@Override
	float getBatteryVoltage();

	/**
	 * Should be serialized as "chargingCurrent"
	 * @return The charging current
	 */
	@JsonProperty("chargingCurrent")
	@Override
	@NotNull Float getChargingCurrent();

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
	@Override
	default @NotNull Integer getControllerTemperatureCelsius(){
		return convertRawTemperature(getControllerTemperatureRaw());
	}
	/**
	 * @return The temperature of the battery in degrees celsius
	 */
	@Override
	default @NotNull Integer getBatteryTemperatureCelsius(){
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
	@JsonProperty("loadVoltage")
	@GraphQLExclude
	float getLoadVoltageRaw();

	/**
	 * Should be serialized as "loadCurrent"
	 * <p>
	 * AKA street light current
	 * @return The load/street light current in amps
	 */
	@JsonProperty("loadCurrent")
	@GraphQLExclude
	float getLoadCurrentRaw();

	/**
	 * Should be serialized as "loadPower"
	 * <p>
	 * AKA street light power
	 * @return The load/street light power in watts
	 */
	@JsonProperty("loadPower")
	@GraphQLExclude
	int getLoadPowerRaw();

	// region load/generator getters
	@GraphQLInclude("loadVoltage")
	@RoverOnly
	default float getLoadVoltage() {
		if (!isDcdc()) {
			return getLoadVoltageRaw();
		}
		return 0;
	}
	@GraphQLInclude("loadCurrent")
	@RoverOnly
	default float getLoadCurrent() {
		if (!isDcdc()) {
			return getLoadCurrentRaw();
		}
		return 0;
	}
	@GraphQLInclude("loadPower")
	@RoverOnly
	default int getLoadPower() {
		if (!isDcdc()) {
			return getLoadPowerRaw();
		}
		return 0;
	}
	@GraphQLInclude("generatorVoltage")
	@DcdcOnly
	default float getGeneratorVoltage() {
		if (isDcdc()) {
			return getLoadVoltageRaw();
		}
		return 0;
	}
	@GraphQLInclude("generatorCurrent")
	@DcdcOnly
	default float getGeneratorCurrent() {
		if (isDcdc()) {
			return getLoadCurrentRaw();
		}
		return 0;
	}
	@GraphQLInclude("generatorPower")
	@DcdcOnly
	default int getGeneratorPower() {
		if (isDcdc()) {
			return getLoadPowerRaw();
		}
		return 0;
	}
	// endregion

	/** AKA PV/Solar Panel voltage*/
	@SerializeNameDefinedInBase
	@Override
	@NotNull Float getPVVoltage();

	@SerializeNameDefinedInBase
	@Override
	@NotNull Float getPVCurrent();

	@JsonProperty("chargingPower")
	@Override
	@NotNull Integer getChargingPower();

	@SerializeNameDefinedInBase
	@ResetEvening
	@Override
	float getDailyMinBatteryVoltage();

	@SerializeNameDefinedInBase
	@ResetMorning
	@Override
	float getDailyMaxBatteryVoltage();

	/**
	 * @return The daily record for the maximum charger current
	 */
	@ResetMorning
	@JsonProperty("dailyMaxChargingCurrent")
	float getDailyMaxChargingCurrent();

	/**
	 * @return The daily record for the maximum discharging current
	 */
	@ResetMorning
	@JsonProperty("dailyMaxDischargingCurrent")
	float getDailyMaxDischargingCurrent();

	@ResetMorning
	@JsonProperty("dailyMaxChargingPower")
	int getDailyMaxChargingPower();

	@ResetMorning
	@JsonProperty("dailyMaxDischargingPower")
	int getDailyMaxDischargingPower();

	@ResetMorning
	@JsonProperty("dailyAH")
	@Override
	int getDailyAH();
	@JsonProperty("dailyAHDischarging")
	int getDailyAHDischarging();

	// 0x0113
	@SerializeNameDefinedInBase
	@ResetMorning
	@Override
	float getDailyKWH();
	@SerializeNameDefinedInBase
	@Override
	float getDailyKWHConsumption();

	@ResetEvening
	@JsonProperty("operatingDaysCount")
	int getOperatingDaysCount();
	@JsonProperty("batteryOverDischargesCount")
	int getBatteryOverDischargesCount();
	@JsonProperty("batteryFullChargesCount")
	int getBatteryFullChargesCount();
	@ValidSinceVersion(version = RoverStatusPacket.VERSION_CORRECT_TWO_REGISTER, probablyValidAnyway = true)
	@JsonProperty("chargingAmpHoursOfBatteryCount")
	int getChargingAmpHoursOfBatteryCount();
	@ValidSinceVersion(version = RoverStatusPacket.VERSION_CORRECT_TWO_REGISTER, probablyValidAnyway = true)
	@JsonProperty("dischargingAmpHoursOfBatteryCount")
	int getDischargingAmpHoursOfBatteryCount();
	@ValidSinceVersion(version = RoverStatusPacket.VERSION_CORRECT_TWO_REGISTER)
	@Override
	float getCumulativeKWH();
	@ValidSinceVersion(version = RoverStatusPacket.VERSION_CORRECT_TWO_REGISTER, probablyValidAnyway = true)
	@Override
	float getCumulativeKWHConsumption();

	/**
	 * @return The raw street light value. On DCC Charge Controllers, this may be undefined as its register is "reserved"
	 */
	@JsonProperty("streetLightValue")
	@RoverOnly
	int getRawStreetLightValue();
	@RoverOnly
	default int getStreetLightStatusValue(){
		return ~StreetLight.IGNORED_BITS & getRawStreetLightValue();
	}
	@RoverOnly
	default StreetLight getStreetLightStatus(){ return Modes.getActiveMode(StreetLight.class, getStreetLightStatusValue()); }
	@JsonProperty("streetLightBrightness") // convenient
	@RoverOnly
	default int getStreetLightBrightnessPercent(){ return StreetLight.getBrightnessValue(getRawStreetLightValue()); }
	@JsonProperty("streetLightOn") // convenient
	@RoverOnly
	default boolean isStreetLightOn(){ return getStreetLightStatus() == StreetLight.ON; }

	@JsonProperty("chargingState")
	int getChargingStateValue();
	@GraphQLInclude("chargingMode")
	@Override
	default @NotNull ChargingState getChargingMode(){ return Modes.getActiveMode(ChargingState.class, getChargingStateValue()); }
	@JsonProperty("chargingStateName") // convenient
	default String getChargingStateName(){ return getChargingMode().getModeName(); }

	@ValidSinceVersion(version = RoverStatusPacket.VERSION_CORRECT_TWO_REGISTER)
	@JsonProperty("errorMode")
	@Override
	int getErrorModeValue();
	@ValidSinceVersion(version = RoverStatusPacket.VERSION_CORRECT_TWO_REGISTER)
	@Override
	default Collection<? extends SimpleRoverErrorMode> getErrorModes(){
		if (isDcdc()) {
			return getDcdcErrorModes();
		}
		return getRoverErrorModes();
	}
	@ValidSinceVersion(version = RoverStatusPacket.VERSION_CORRECT_TWO_REGISTER)
	@JsonProperty("errors")
	default String getErrorsString(){
		if (isDcdc()) {
			return Modes.toString(DcdcErrorMode.class, getErrorModeValue());
		}
		return Modes.toString(RoverErrorMode.class, getErrorModeValue());
	}
	@ValidSinceVersion(version = RoverStatusPacket.VERSION_CORRECT_TWO_REGISTER)
	@GraphQLInclude("dcdcErrorModes")
	@DcdcOnly
	default Collection<DcdcErrorMode> getDcdcErrorModes() {
		return Modes.getActiveModes(DcdcErrorMode.class, getErrorModeValue());
	}
	@ValidSinceVersion(version = RoverStatusPacket.VERSION_CORRECT_TWO_REGISTER)
	@GraphQLInclude("roverErrorModes")
	@RoverOnly
	default Collection<RoverErrorMode> getRoverErrorModes() {
		return Modes.getActiveModes(RoverErrorMode.class, getErrorModeValue());
	}
	@ValidSinceVersion(version = RoverStatusPacket.VERSION_CORRECT_TWO_REGISTER)
	@GraphQLInclude("dcdcErrorModesOrEmpty")
	@JsonPropertyDescription("The DcdcErrorMode or an empty list if this is not a DCDC charge controller")
	default Collection<DcdcErrorMode> getDcdcErrorModesOrEmtpy() {
		if (isDcdc()) {
			return getDcdcErrorModes();
		}
		return Collections.emptyList();
	}
	@ValidSinceVersion(version = RoverStatusPacket.VERSION_CORRECT_TWO_REGISTER)
	@GraphQLInclude("roverErrorModesOrEmpty")
	@JsonPropertyDescription("The RoverErrorModes or an empty list if this is a DCDC charge controller")
	default Collection<RoverErrorMode> getRoverErrorModesOrEmtpy() {
		if (!isDcdc()) {
			return getRoverErrorModes();
		}
		return Collections.emptyList();
	}

	// Start of E000s

	/**
	 * Only applies to Dual Input Charge Controllers
	 *
	 * Note: On my Rover 40A I got a value of 100 (1A).
	 * @return A value from 100 to 5000 or null
	 */
	@DcdcOnly
	@WillBeUsedEventually
	default @Nullable Integer getChargingCurrentSettingRaw() { return null; }

	/**
	 *
	 * @return A value from 1A to 50A or null
	 */
	@DcdcOnly
	@WillBeUsedEventually
	default @Nullable Float getChargingCurrentSetting() {
		Integer raw = getChargingCurrentSettingRaw();
		return raw == null ? null : raw / 100.0f;
	}

	/**
	 *
	 * @return The nominal battery capacity in AmpHours (AH)
	 */
	@JsonProperty("nominalBatteryCapacity")
	@JsonPropertyDescription("Nominal battery capacity in AH. Usually this is not accurate.")
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
	@GraphQLInclude("batteryTypeValue")
	@JsonProperty("batteryType")
	int getBatteryTypeValue();
	@GraphQLInclude("batteryType")
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
	@JsonPropertyDescription("Usually does not represent any meaningful value")
	int getLightControlDelayMinutes();
	@JsonProperty("lightControlVoltage")
	@JsonPropertyDescription("Usually does not represent any meaningful value")
	int getLightControlVoltage();

	// 0xE020
	@JsonProperty("ledLoadCurrentSettingRaw")
	int getLEDLoadCurrentSettingRaw();
	/** Units: mA */
	default int getLEDLoadCurrentSettingMilliAmps(){ return getLEDLoadCurrentSettingRaw() * 10; }

	@JsonProperty("specialPowerControlE021Raw")
	@RoverOnly
	int getSpecialPowerControlE021Raw();
	@RoverOnly
	default SpecialPowerControl_E021 getSpecialPowerControlE021(){ return new ImmutableSpecialPowerControl_E021(getSpecialPowerControlE021Raw()); }

	// For the Rover Elite, everything below here isn't gonna work because it doesn't have a load.

	// region MES Load
	@RoverOnly
	@Nullable Integer getWorkingHoursRaw(Sensing sensing);
	@Deprecated default int getWorkingHours(Sensing sensing){ return requireNonNull(getWorkingHoursRaw(sensing)) + 1; }

	@RoverOnly
	@Nullable Integer getPowerWithPeopleSensedRaw(Sensing sensing);
	@Deprecated default int getPowerWithPeopleSensedPercentage(Sensing sensing){ return requireNonNull(getPowerWithPeopleSensedRaw(sensing)) + 10; }

	@RoverOnly
	@Nullable Integer getPowerWithNoPeopleSensedRaw(Sensing sensing);
	@Deprecated default int getPowerWithNoPeopleSensedPercentage(Sensing sensing){ return requireNonNull(getPowerWithNoPeopleSensedRaw(sensing)) + 10; }

	@RoverOnly
	default @Nullable SensingBundle getSensingBundle(Sensing sensing){
		Integer workingHoursRaw = getWorkingHoursRaw(sensing);
		Integer powerWithPeopleSensedRaw = getPowerWithPeopleSensedRaw(sensing);
		Integer powerWithNoPeopleSensedRaw = getPowerWithNoPeopleSensedRaw(sensing);
		if (workingHoursRaw == null || powerWithPeopleSensedRaw == null || powerWithNoPeopleSensedRaw == null) {
			return null;
		}
		return new SensingBundle(workingHoursRaw, powerWithPeopleSensedRaw, powerWithNoPeopleSensedRaw);
	}
	@JsonProperty("sensed1")
	default @Nullable SensingBundle getSensed1(){ return getSensingBundle(Sensing.SENSING_1); }
	@JsonProperty("sensed2")
	default @Nullable SensingBundle getSensed2(){ return getSensingBundle(Sensing.SENSING_2); }
	@JsonProperty("sensed3")
	default @Nullable SensingBundle getSensed3(){ return getSensingBundle(Sensing.SENSING_3); }


	@JsonProperty("sensingTimeDelayRaw")
	@Nullable Integer getSensingTimeDelayRaw();
	default @Nullable Integer getSensingTimeDelaySeconds(){
		Integer raw = getSensingTimeDelayRaw();
		return raw == null ? null : raw + 10;
	}

	@JsonProperty("ledLoadCurrentRaw")
	@Nullable Integer getLEDLoadCurrentRaw();
	/** Units: mA */
	default @Nullable Integer getLEDLoadCurrentMilliAmps(){
		Integer raw = getLEDLoadCurrentRaw();
		return raw == null ? null : raw * 10;
	}

	@JsonProperty("specialPowerControlE02DRaw")
	@RoverOnly
	@Nullable Integer getSpecialPowerControlE02DRaw();
	default @Nullable SpecialPowerControl_E02D getSpecialPowerControlE02D(){
		Integer raw = getSpecialPowerControlE02DRaw();
		return raw == null ? null : new ImmutableSpecialPowerControl_E02D(raw);
	}
	// endregion

	/**
	 * Only applies to Dual Input Charge Controllers
	 *
	 * On my rover 40A, got value of 4
	 *
	 * The default is 100
	 * @return And integer representing the percentage setting or null
	 */
	@DcdcOnly
	@WillBeUsedEventually
	default Integer getControllerChargingPowerSetting() { return null; }
	/**
	 * Only applies to Dual Input Charge Controllers
	 *
	 * On my rover 40A, got value of 4, then 243
	 *
	 * The default is 100
	 * @return And integer representing the percentage setting or null
	 */
	@DcdcOnly
	@WillBeUsedEventually
	default Integer getGeneratorChargingPowerSetting() { return null; }

	@DefaultFinal
	default boolean supportsMesLoad() {
		return getWorkingHoursRaw(Sensing.SENSING_1) != null;
	}

}
