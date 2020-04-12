package me.retrodaredevil.solarthing.solar.renogy.rover;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(value = {"productModelString", "hardwareVersionString", "softwareVersionString", "streetLightBrightness", "streetLightOn", "chargingStateName", "errors", "batteryTypeName", "loadWorkingModeName"}, allowGetters = true)
public class ImmutableRoverStatusPacket implements RoverStatusPacket {
	private final RoverIdentifier identifier;
	private final int maxVoltage;
	private final int ratedChargingCurrent;
	private final int ratedDischargingCurrent;
	private final int productType;
	private final byte[] productModel;
	private final int softwareVersion;
	private final int hardwareVersion;
	private final int productSerialNumber;
	private final int controllerDeviceAddress;
	private final int batteryCapacitySOC;
	private final float batteryVoltage;
	private final float chargingCurrent;
	private final int controllerTemperatureRaw;
	private final int batteryTemperatureRaw;
	private final float loadVoltage;
	private final float loadCurrent;
	private final int loadPower;
	private final float inputVoltage; // pv voltage
	private final float pvCurrent;
	private final int chargingPower;
	private final float dailyMinBatteryVoltage;
	private final float dailyMaxBatteryVoltage;
	private final float dailyMaxChargingCurrent;
	private final float dailyMaxDischargingCurrent;
	private final int dailyMaxChargingPower;
	private final int dailyMaxDischargingPower;
	private final int dailyAH; // daily charging AH
	private final int dailyAHDischarging;
	private final float dailyKWH;
	private final float dailyKWHConsumption;
	private final int operatingDaysCount;
	private final int batteryOverDischargesCount;
	private final int batteryFullChargesCount;
	private final int chargingAmpHoursOfBatteryCount;
	private final int dischargingAmpHoursOfBatteryCount;
	private final float cumulativeKWH;
	private final float cumulativeKWHConsumption;
	private final int streetLightValue;
	private final int chargingState;
	private final int errorMode;
	private final int nominalBatteryCapacity;
	private final int systemVoltageSetting;
	private final int recognizedVoltage;
	private final int batteryType;

	private final int overVoltageThresholdRaw;
	private final int chargingVoltageLimitRaw;
	private final int equalizingChargingVoltageRaw;
	private final int boostChargingVoltageRaw;
	private final int floatingChargingVoltageRaw;
	private final int boostChargingRecoveryVoltageRaw;
	private final int overDischargeRecoveryVoltageRaw;
	private final int underVoltageWarningLevelRaw;
	private final int overDischargeVoltageRaw;
	private final int dischargingLimitVoltageRaw;

	private final int endOfChargeSOC;
	private final int endOfDischargeSOC;
	private final int overDischargeTimeDelaySeconds;
	private final int equalizingChargingTimeRaw;
	private final int boostChargingTimeRaw;
	private final int equalizingChargingIntervalRaw;
	private final int temperatureCompensationFactorRaw;

	private final OperatingSettingBundle operatingStage1, operatingStage2, operatingStage3, operatingMorningOn;

	private final int loadWorkingMode;
	private final int lightControlDelayMinutes;
	private final int lightControlVoltage;
	private final int ledLoadCurrentSettingRaw;
	private final int specialPowerControlE021Raw; // maybe add convenient?

	private final SensingBundle sensed1, sensed2, sensed3;

	private final int sensingTimeDelayRaw;
	private final int ledLoadCurrentRaw;
	private final int specialPowerControlE02DRaw;


	@JsonCreator
	public ImmutableRoverStatusPacket(
			@JsonProperty(value = "maxVoltage", required = true) int maxVoltage, @JsonProperty(value = "ratedChargingCurrent", required = true) int ratedChargingCurrent, @JsonProperty(value = "ratedDischargingCurrent", required = true) int ratedDischargingCurrent, @JsonProperty(value = "productType", required = true) int productType, @JsonProperty(value = "productModelEncoded", required = true) byte[] productModel,
			@JsonProperty(value = "softwareVersion", required = true) int softwareVersion, @JsonProperty(value = "hardwareVersion", required = true) int hardwareVersion, @JsonProperty(value = "productSerialNumber", required = true) int productSerialNumber,
			@JsonProperty(value = "controllerDeviceAddress", required = true) int controllerDeviceAddress,
			@JsonProperty(value = "batteryCapacitySOC", required = true) int batteryCapacitySOC, @JsonProperty(value = "batteryVoltage", required = true) float batteryVoltage, @JsonProperty(value = "chargingCurrent", required = true) float chargingCurrent, @JsonProperty(value = "controllerTemperatureRaw", required = true) int controllerTemperatureRaw, @JsonProperty(value = "batteryTemperatureRaw", required = true) int batteryTemperatureRaw,
			@JsonProperty(value = "loadVoltage", required = true) float loadVoltage, @JsonProperty(value = "loadCurrent", required = true) float loadCurrent, @JsonProperty(value = "loadPower", required = true) int loadPower, @JsonProperty(value = "inputVoltage", required = true) float inputVoltage, @JsonProperty(value = "pvCurrent", required = true) float pvCurrent,
			@JsonProperty(value = "chargingPower", required = true) int chargingPower, @JsonProperty(value = "dailyMinBatteryVoltage", required = true) float dailyMinBatteryVoltage, @JsonProperty(value = "dailyMaxBatteryVoltage", required = true) float dailyMaxBatteryVoltage,
			@JsonProperty(value = "dailyMaxChargingCurrent", required = true) float dailyMaxChargingCurrent, @JsonProperty(value = "dailyMaxDischargingCurrent", required = true) float dailyMaxDischargingCurrent, @JsonProperty(value = "dailyMaxChargingPower", required = true) int dailyMaxChargingPower, @JsonProperty(value = "dailyMaxDischargingPower", required = true) int dailyMaxDischargingPower,
			@JsonProperty(value = "dailyAH", required = true) int dailyAH, @JsonProperty(value = "dailyAHDischarging", required = true) int dailyAHDischarging,
			@JsonProperty(value = "dailyKWH", required = true) float dailyKWH, @JsonProperty(value = "dailyKWHConsumption", required = true) float dailyKWHConsumption,
			@JsonProperty(value = "operatingDaysCount", required = true) int operatingDaysCount, @JsonProperty(value = "batteryOverDischargesCount", required = true) int batteryOverDischargesCount, @JsonProperty(value = "batteryFullChargesCount", required = true) int batteryFullChargesCount,
			@JsonProperty(value = "chargingAmpHoursOfBatteryCount", required = true) int chargingAmpHoursOfBatteryCount, @JsonProperty(value = "dischargingAmpHoursOfBatteryCount", required = true) int dischargingAmpHoursOfBatteryCount,
			@JsonProperty(value = "cumulativeKWH", required = true) float cumulativeKWH, @JsonProperty(value = "cumulativeKWHConsumption", required = true) float cumulativeKWHConsumption,
			@JsonProperty(value = "streetLightValue", required = true) int streetLightValue, @JsonProperty(value = "chargingState", required = true) int chargingState, @JsonProperty(value = "errorMode", required = true) int errorMode, @JsonProperty(value = "nominalBatteryCapacity", required = true) int nominalBatteryCapacity,
			@JsonProperty(value = "systemVoltageSetting", required = true) int systemVoltageSetting, @JsonProperty(value = "recognizedVoltage", required = true) int recognizedVoltage, @JsonProperty(value = "batteryType", required = true) int batteryType,
			@JsonProperty(value = "overVoltageThresholdRaw", required = true) int overVoltageThresholdRaw, @JsonProperty(value = "chargingVoltageLimitRaw", required = true) int chargingVoltageLimitRaw, @JsonProperty(value = "equalizingChargingVoltageRaw", required = true) int equalizingChargingVoltageRaw,
			@JsonProperty(value = "boostChargingVoltageRaw", required = true) int boostChargingVoltageRaw, @JsonProperty(value = "floatingChargingVoltageRaw", required = true) int floatingChargingVoltageRaw, @JsonProperty(value = "boostChargingRecoveryVoltageRaw", required = true) int boostChargingRecoveryVoltageRaw,
			@JsonProperty(value = "overDischargeRecoveryVoltageRaw", required = true) int overDischargeRecoveryVoltageRaw, @JsonProperty(value = "underVoltageWarningLevelRaw", required = true) int underVoltageWarningLevelRaw, @JsonProperty(value = "overDischargeVoltageRaw", required = true) int overDischargeVoltageRaw,
			@JsonProperty(value = "dischargingLimitVoltageRaw", required = true) int dischargingLimitVoltageRaw,
			@JsonProperty(value = "endOfChargeSOC", required = true) int endOfChargeSOC, @JsonProperty(value = "endOfDischargeSOC", required = true) int endOfDischargeSOC,
			@JsonProperty(value = "overDischargeTimeDelaySeconds", required = true) int overDischargeTimeDelaySeconds, @JsonProperty(value = "equalizingChargingTimeRaw", required = true) int equalizingChargingTimeRaw,
			@JsonProperty(value = "boostChargingTimeRaw", required = true) int boostChargingTimeRaw, @JsonProperty(value = "equalizingChargingIntervalRaw", required = true) int equalizingChargingIntervalRaw, @JsonProperty(value = "temperatureCompensationFactorRaw", required = true) int temperatureCompensationFactorRaw,
			@JsonProperty(value = "operatingStage1", required = true) OperatingSettingBundle operatingStage1, @JsonProperty(value = "operatingStage2", required = true) OperatingSettingBundle operatingStage2, @JsonProperty(value = "operatingStage3", required = true) OperatingSettingBundle operatingStage3, @JsonProperty(value = "operatingMorningOn", required = true) OperatingSettingBundle operatingMorningOn, @JsonProperty(value = "loadWorkingMode", required = true) int loadWorkingMode, @JsonProperty(value = "lightControlDelayMinutes", required = true) int lightControlDelayMinutes, @JsonProperty(value = "lightControlVoltage", required = true) int lightControlVoltage, @JsonProperty(value = "ledLoadCurrentSettingRaw", required = true) int ledLoadCurrentSettingRaw,
			@JsonProperty(value = "specialPowerControlE021Raw", required = true) int specialPowerControlE021Raw, @JsonProperty(value = "sensed1", required = true) SensingBundle sensed1, @JsonProperty(value = "sensed2", required = true) SensingBundle sensed2, @JsonProperty(value = "sensed3", required = true) SensingBundle sensed3, @JsonProperty(value = "sensingTimeDelayRaw", required = true) int sensingTimeDelayRaw, @JsonProperty(value = "ledLoadCurrentRaw", required = true) int ledLoadCurrentRaw, @JsonProperty(value = "specialPowerControlE02DRaw", required = true) int specialPowerControlE02DRaw
	) {
		// region initialization
		this.maxVoltage = maxVoltage;
		this.ratedChargingCurrent = ratedChargingCurrent;
		this.ratedDischargingCurrent = ratedDischargingCurrent;
		this.productType = productType;
		this.productModel = productModel;
		this.softwareVersion = softwareVersion;
		this.hardwareVersion = hardwareVersion;
		this.productSerialNumber = productSerialNumber;
		this.controllerDeviceAddress = controllerDeviceAddress;
		this.batteryCapacitySOC = batteryCapacitySOC;
		this.batteryVoltage = batteryVoltage;
		this.chargingCurrent = chargingCurrent;
		this.controllerTemperatureRaw = controllerTemperatureRaw;
		this.batteryTemperatureRaw = batteryTemperatureRaw;
		this.loadVoltage = loadVoltage;
		this.loadCurrent = loadCurrent;
		this.loadPower = loadPower;
		this.inputVoltage = inputVoltage;
		this.pvCurrent = pvCurrent;
		this.chargingPower = chargingPower;
		this.dailyMinBatteryVoltage = dailyMinBatteryVoltage;
		this.dailyMaxBatteryVoltage = dailyMaxBatteryVoltage;
		this.dailyMaxChargingCurrent = dailyMaxChargingCurrent;
		this.dailyMaxDischargingCurrent = dailyMaxDischargingCurrent;
		this.dailyMaxChargingPower = dailyMaxChargingPower;
		this.dailyMaxDischargingPower = dailyMaxDischargingPower;
		this.dailyAH = dailyAH;
		this.dailyAHDischarging = dailyAHDischarging;
		this.dailyKWH = dailyKWH;
		this.dailyKWHConsumption = dailyKWHConsumption;
		this.operatingDaysCount = operatingDaysCount;
		this.batteryOverDischargesCount = batteryOverDischargesCount;
		this.batteryFullChargesCount = batteryFullChargesCount;
		this.chargingAmpHoursOfBatteryCount = chargingAmpHoursOfBatteryCount;
		this.dischargingAmpHoursOfBatteryCount = dischargingAmpHoursOfBatteryCount;
		this.cumulativeKWH = cumulativeKWH;
		this.cumulativeKWHConsumption = cumulativeKWHConsumption;
		this.streetLightValue = streetLightValue;
		this.chargingState = chargingState;
		this.errorMode = errorMode;
		this.nominalBatteryCapacity = nominalBatteryCapacity;
		this.systemVoltageSetting = systemVoltageSetting;
		this.recognizedVoltage = recognizedVoltage;
		this.batteryType = batteryType;
		this.overVoltageThresholdRaw = overVoltageThresholdRaw;
		this.chargingVoltageLimitRaw = chargingVoltageLimitRaw;
		this.equalizingChargingVoltageRaw = equalizingChargingVoltageRaw;
		this.boostChargingVoltageRaw = boostChargingVoltageRaw;
		this.floatingChargingVoltageRaw = floatingChargingVoltageRaw;
		this.boostChargingRecoveryVoltageRaw = boostChargingRecoveryVoltageRaw;
		this.overDischargeRecoveryVoltageRaw = overDischargeRecoveryVoltageRaw;
		this.underVoltageWarningLevelRaw = underVoltageWarningLevelRaw;
		this.overDischargeVoltageRaw = overDischargeVoltageRaw;
		this.dischargingLimitVoltageRaw = dischargingLimitVoltageRaw;
		this.endOfChargeSOC = endOfChargeSOC;
		this.endOfDischargeSOC = endOfDischargeSOC;
		this.overDischargeTimeDelaySeconds = overDischargeTimeDelaySeconds;
		this.equalizingChargingTimeRaw = equalizingChargingTimeRaw;
		this.boostChargingTimeRaw = boostChargingTimeRaw;
		this.equalizingChargingIntervalRaw = equalizingChargingIntervalRaw;
		this.temperatureCompensationFactorRaw = temperatureCompensationFactorRaw;
		this.operatingStage1 = operatingStage1;
		this.operatingStage2 = operatingStage2;
		this.operatingStage3 = operatingStage3;
		this.operatingMorningOn = operatingMorningOn;
		this.loadWorkingMode = loadWorkingMode;
		this.lightControlDelayMinutes = lightControlDelayMinutes;
		this.lightControlVoltage = lightControlVoltage;
		this.ledLoadCurrentSettingRaw = ledLoadCurrentSettingRaw;
		this.specialPowerControlE021Raw = specialPowerControlE021Raw;
		this.sensed1 = sensed1;
		this.sensed2 = sensed2;
		this.sensed3 = sensed3;
		this.sensingTimeDelayRaw = sensingTimeDelayRaw;
		this.ledLoadCurrentRaw = ledLoadCurrentRaw;
		this.specialPowerControlE02DRaw = specialPowerControlE02DRaw;
		// endregion

		identifier = new RoverIdentifier(productSerialNumber);
	}

	@NotNull
    @Override
	public RoverIdentifier getIdentifier() {
		return identifier;
	}

	@Override
	public int getMaxVoltageValue() {
		return maxVoltage;
	}

	@Override
	public int getRatedChargingCurrentValue() {
		return ratedChargingCurrent;
	}

	@Override
	public int getRatedDischargingCurrentValue() {
		return ratedDischargingCurrent;
	}

	@Override
	public int getProductTypeValue() {
		return productType;
	}

	@Override
	public byte[] getProductModelValue() {
		return productModel;
	}

	@Override
	public int getSoftwareVersionValue() {
		return softwareVersion;
	}

	@Override
	public int getHardwareVersionValue() {
		return hardwareVersion;
	}

	@Override
	public int getProductSerialNumber() {
		return productSerialNumber;
	}

	@Override
	public int getControllerDeviceAddress() {
		return controllerDeviceAddress;
	}

	@Override
	public int getBatteryCapacitySOC() {
		return batteryCapacitySOC;
	}

	@Override
	public float getBatteryVoltage() {
		return batteryVoltage;
	}

	@Override
	public Float getChargingCurrent() {
		return chargingCurrent;
	}

	@Override
	public int getControllerTemperatureRaw() {
		return controllerTemperatureRaw;
	}

	@Override
	public int getBatteryTemperatureRaw() {
		return batteryTemperatureRaw;
	}

	@Override
	public float getLoadVoltage() {
		return loadVoltage;
	}

	@Override
	public float getLoadCurrent() {
		return loadCurrent;
	}

	@Override
	public int getLoadPower() {
		return loadPower;
	}

	@NotNull
    @Override
	public Float getInputVoltage() {
		return inputVoltage;
	}

	@NotNull
	@Override
	public Float getPVCurrent() {
		return pvCurrent;
	}

	@Override
	public Integer getChargingPower() {
		return chargingPower;
	}

	@Override
	public float getDailyMinBatteryVoltage() {
		return dailyMinBatteryVoltage;
	}

	@Override
	public float getDailyMaxBatteryVoltage() {
		return dailyMaxBatteryVoltage;
	}

	@Override
	public float getDailyMaxChargingCurrent() {
		return dailyMaxChargingCurrent;
	}

	@Override
	public float getDailyMaxDischargingCurrent() {
		return dailyMaxDischargingCurrent;
	}

	@Override
	public int getDailyMaxChargingPower() {
		return dailyMaxChargingPower;
	}

	@Override
	public int getDailyMaxDischargingPower() {
		return dailyMaxDischargingPower;
	}

	@Override
	public int getDailyAH() {
		return dailyAH;
	}

	@Override
	public int getDailyAHDischarging() {
		return dailyAHDischarging;
	}

	@Override
	public float getDailyKWH() {
		return dailyKWH;
	}

	@Override
	public float getDailyKWHConsumption() {
		return dailyKWHConsumption;
	}

	@Override
	public int getOperatingDaysCount() {
		return operatingDaysCount;
	}

	@Override
	public int getBatteryOverDischargesCount() {
		return batteryOverDischargesCount;
	}

	@Override
	public int getBatteryFullChargesCount() {
		return batteryFullChargesCount;
	}

	@Override
	public int getChargingAmpHoursOfBatteryCount() {
		return chargingAmpHoursOfBatteryCount;
	}

	@Override
	public int getDischargingAmpHoursOfBatteryCount() {
		return dischargingAmpHoursOfBatteryCount;
	}

	@Override
	public float getCumulativeKWH() {
		return cumulativeKWH;
	}

	@Override
	public float getCumulativeKWHConsumption() {
		return cumulativeKWHConsumption;
	}

	@Override
	public int getRawStreetLightValue() {
		return streetLightValue;
	}

	@Override
	public int getChargingStateValue() {
		return chargingState;
	}

	@Override
	public int getErrorModeValue() {
		return errorMode;
	}

	@Override
	public int getNominalBatteryCapacity() {
		return nominalBatteryCapacity;
	}

	@Override
	public int getSystemVoltageSettingValue() {
		return systemVoltageSetting;
	}

	@Override
	public int getRecognizedVoltageValue() {
		return recognizedVoltage;
	}

	@Override
	public int getBatteryTypeValue() {
		return batteryType;
	}

	@Override
	public int getOverVoltageThresholdRaw() {
		return overVoltageThresholdRaw;
	}

	@Override
	public int getChargingVoltageLimitRaw() {
		return chargingVoltageLimitRaw;
	}

	@Override
	public int getEqualizingChargingVoltageRaw() {
		return equalizingChargingVoltageRaw;
	}

	@Override
	public int getBoostChargingVoltageRaw() {
		return boostChargingVoltageRaw;
	}

	@Override
	public int getFloatingChargingVoltageRaw() {
		return floatingChargingVoltageRaw;
	}

	@Override
	public int getBoostChargingRecoveryVoltageRaw() {
		return boostChargingRecoveryVoltageRaw;
	}

	@Override
	public int getOverDischargeRecoveryVoltageRaw() {
		return overDischargeRecoveryVoltageRaw;
	}

	@Override
	public int getUnderVoltageWarningLevelRaw() {
		return underVoltageWarningLevelRaw;
	}

	@Override
	public int getOverDischargeVoltageRaw() {
		return overDischargeVoltageRaw;
	}

	@Override
	public int getDischargingLimitVoltageRaw() {
		return dischargingLimitVoltageRaw;
	}

	@Override
	public int getEndOfChargeSOC() {
		return endOfChargeSOC;
	}

	@Override
	public int getEndOfDischargeSOC() {
		return endOfDischargeSOC;
	}

	@Override
	public int getOverDischargeTimeDelaySeconds() {
		return overDischargeTimeDelaySeconds;
	}

	@Override
	public int getEqualizingChargingTimeRaw() {
		return equalizingChargingTimeRaw;
	}

	@Override
	public int getBoostChargingTimeRaw() {
		return boostChargingTimeRaw;
	}

	@Override
	public int getEqualizingChargingIntervalRaw() {
		return equalizingChargingIntervalRaw;
	}

	@Override
	public int getTemperatureCompensationFactorRaw() {
		return temperatureCompensationFactorRaw;
	}

	@Override
	public int getOperatingDurationHours(OperatingSetting setting) {
		switch(setting){
			case STAGE_1: return operatingStage1.getDurationHours();
			case STAGE_2: return operatingStage2.getDurationHours();
			case STAGE_3: return operatingStage3.getDurationHours();
			case MORNING_ON: return operatingMorningOn.getDurationHours();
			default: throw new UnsupportedOperationException(setting.toString());
		}
	}

	@Override
	public int getOperatingPowerPercentage(OperatingSetting setting) {
		switch(setting){
			case STAGE_1: return operatingStage1.getOperatingPowerPercentage();
			case STAGE_2: return operatingStage2.getOperatingPowerPercentage();
			case STAGE_3: return operatingStage3.getOperatingPowerPercentage();
			case MORNING_ON: return operatingMorningOn.getOperatingPowerPercentage();
			default: throw new UnsupportedOperationException(setting.toString());
		}
	}

	@Override
	public OperatingSettingBundle getOperatingSettingBundle(OperatingSetting setting) {
		switch(setting){
			case STAGE_1: return operatingStage1;
			case STAGE_2: return operatingStage2;
			case STAGE_3: return operatingStage3;
			case MORNING_ON: return operatingMorningOn;
			default: throw new UnsupportedOperationException(setting.toString());
		}
	}

	@Override
	public int getLoadWorkingModeValue() {
		return loadWorkingMode;
	}

	@Override
	public int getLightControlDelayMinutes() {
		return lightControlDelayMinutes;
	}

	@Override
	public int getLightControlVoltage() {
		return lightControlVoltage;
	}

	@Override
	public int getLEDLoadCurrentSettingRaw() {
		return ledLoadCurrentSettingRaw;
	}

	@Override
	public int getSpecialPowerControlE021Raw() {
		return specialPowerControlE021Raw;
	}

	@Override
	public int getWorkingHoursRaw(Sensing sensing) {
		switch(sensing){
			case SENSING_1: return sensed1.getWorkingHoursRaw();
			case SENSING_2: return sensed2.getWorkingHoursRaw();
			case SENSING_3: return sensed3.getWorkingHoursRaw();
			default: throw new UnsupportedOperationException(sensing.toString());
		}
	}

	@Override
	public int getPowerWithPeopleSensedRaw(Sensing sensing) {
		switch(sensing){
			case SENSING_1: return sensed1.getPowerWithPeopleSensedRaw();
			case SENSING_2: return sensed2.getPowerWithPeopleSensedRaw();
			case SENSING_3: return sensed3.getPowerWithPeopleSensedRaw();
			default: throw new UnsupportedOperationException(sensing.toString());
		}
	}

	@Override
	public int getPowerWithNoPeopleSensedRaw(Sensing sensing) {
		switch(sensing){
			case SENSING_1: return sensed1.getPowerWithNoPeopleSensedRaw();
			case SENSING_2: return sensed2.getPowerWithNoPeopleSensedRaw();
			case SENSING_3: return sensed3.getPowerWithNoPeopleSensedRaw();
			default: throw new UnsupportedOperationException(sensing.toString());
		}
	}

	@Override
	public SensingBundle getSensingBundle(Sensing sensing) {
		switch(sensing){
			case SENSING_1: return sensed1;
			case SENSING_2: return sensed2;
			case SENSING_3: return sensed3;
			default: throw new UnsupportedOperationException(sensing.toString());
		}
	}
	@Override public SensingBundle getSensed1() { return sensed1; }
	@Override public SensingBundle getSensed2() { return sensed2; }
	@Override public SensingBundle getSensed3() { return sensed3; }

	@Override
	public int getSensingTimeDelayRaw() {
		return sensingTimeDelayRaw;
	}

	@Override
	public int getLEDLoadCurrentRaw() {
		return ledLoadCurrentRaw;
	}

	@Override
	public int getSpecialPowerControlE02DRaw() {
		return specialPowerControlE02DRaw;
	}

}
