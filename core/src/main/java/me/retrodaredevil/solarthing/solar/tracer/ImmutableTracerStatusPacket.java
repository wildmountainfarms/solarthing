package me.retrodaredevil.solarthing.solar.tracer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import me.retrodaredevil.solarthing.packets.identification.NumberedIdentifier;

public class ImmutableTracerStatusPacket implements TracerStatusPacket {
	private final @Nullable Integer packetVersion;
	private final TracerIdentifier identifier;
	private final TracerIdentityInfo identityInfo;

	private final int ratedInputVoltage, ratedInputCurrent, ratedInputPower;
	private final int ratedOutputVoltage, ratedOutputCurrent, ratedOutputPower;
	private final int chargingTypeValue;
	private final int ratedLoadOutputCurrent;
	private final float inputVoltage, pvCurrent, pvWattage;
	private final float batteryVoltage, chargingCurrent, chargingPower;
	private final float loadVoltage, loadCurrent, loadPower;
	private final float batteryTemperatureCelsius, insideControllerTemperatureCelsius, powerComponentTemperatureCelsius;
	private final int batterySOC;
	private final float remoteBatteryTemperatureCelsius;
	private final int realBatteryRatedVoltageValue;
	private final int batteryStatusValue;
	private final int chargingEquipmentStatusValue;
	private final float dailyMaxInputVoltage, dailyMinInputVoltage, dailyMaxBatteryVoltage, dailyMinBatteryVoltage;
	private final float dailyKWHConsumption, monthlyKWHConsumption, yearlyKWHConsumption, cumulativeKWHConsumption;
	private final float dailyKWH, monthlyKWH, yearlyKWH, cumulativeKWH;
	private final float carbonDioxideReductionTons;
	private final float netBatteryCurrent;
	private final float batteryTemperatureCelsius331D, ambientTemperatureCelsius;
	private final int batteryTypeValue;
	private final int batteryCapacityAmpHours;
	private final int temperatureCompensationCoefficient;
	private final float highVoltageDisconnect, chargingLimitVoltage, overVoltageReconnect, equalizationVoltage, boostVoltage, floatVoltage;
	private final float boostReconnectVoltage, lowVoltageReconnect, underVoltageRecover, underVoltageWarning, lowVoltageDisconnect, dischargingLimitVoltage;
	private final long secondMinuteHourDayMonthYearRaw;
	private final int equalizationChargingCycleDays;
	private final float batteryTemperatureWarningUpperLimit, batteryTemperatureWarningLowerLimit;
	private final float insideControllerTemperatureWarningUpperLimit, insideControllerTemperatureWarningUpperLimitRecover;
	private final float powerComponentTemperatureWarningUpperLimit, powerComponentTemperatureWarningUpperLimitRecover;
	private final float lineImpedance;
	private final float nightPVVoltageThreshold;
	private final int lightSignalStartupDelayTime;
	private final float dayPVVoltageThreshold;
	private final int lightSignalTurnOffDelayTime;
	private final int loadControlModeValue;
	private final int workingTimeLength1Raw, workingTimeLength2Raw;
	private final long turnOnTiming1Raw, turnOffTiming1Raw;
	private final long turnOnTiming2Raw, turnOffTiming2Raw;
	private final int lengthOfNightRaw;
	private final int batteryRatedVoltageCode;
	private final int loadTimingControlSelectionValue;
	private final boolean isLoadOnByDefaultInManualMode;
	private final int equalizeDurationMinutes, boostDurationMinutes;
	private final int dischargingPercentage, chargingPercentage;
	private final int batteryManagementModeValue;
	private final boolean isManualLoadControlOn, isLoadTestModeEnabled, isLoadForcedOn;
	private final boolean isInsideControllerOverTemperature, isNight;

	@JsonCreator
	public ImmutableTracerStatusPacket(
			@JsonProperty("packetVersion") @Nullable Integer packetVersion,
			@JsonProperty("number") Integer number,
			@JsonProperty(value = "ratedInputVoltage", required = true) int ratedInputVoltage,
			@JsonProperty(value = "ratedInputCurrent", required = true) int ratedInputCurrent,
			@JsonProperty(value = "ratedInputPower", required = true) int ratedInputPower,
			@JsonProperty(value = "ratedOutputVoltage", required = true) int ratedOutputVoltage,
			@JsonProperty(value = "ratedOutputCurrent", required = true) int ratedOutputCurrent,
			@JsonProperty(value = "ratedOutputPower", required = true) int ratedOutputPower,
			@JsonProperty(value = "chargingTypeValue", required = true) int chargingTypeValue,
			@JsonProperty(value = "ratedLoadOutputCurrent", required = true) int ratedLoadOutputCurrent,
			@JsonProperty(value = "inputVoltage", required = true) float inputVoltage, @JsonProperty(value = "pvCurrent", required = true) float pvCurrent, @JsonProperty(value = "pvWattage", required = true) float pvWattage,
			@JsonProperty(value = "batteryVoltage", required = true) float batteryVoltage, @JsonProperty(value = "chargingCurrent", required = true) float chargingCurrent, @JsonProperty(value = "chargingPower", required = true) float chargingPower,
			@JsonProperty(value = "loadVoltage", required = true) float loadVoltage, @JsonProperty(value = "loadCurrent", required = true) float loadCurrent, @JsonProperty(value = "loadPower", required = true) float loadPower,
			@JsonProperty(value = "batteryTemperatureCelsius", required = true) float batteryTemperatureCelsius, @JsonProperty(value = "insideControllerTemperatureCelsius", required = true) float insideControllerTemperatureCelsius, @JsonProperty(value = "powerComponentTemperatureCelsius", required = true) float powerComponentTemperatureCelsius,
			@JsonProperty(value = "batterySOC", required = true) int batterySOC,
			@JsonProperty(value = "remoteBatteryTemperatureCelsius", required = true) float remoteBatteryTemperatureCelsius,
			@JsonProperty(value = "realBatteryRatedVoltageValue", required = true) int realBatteryRatedVoltageValue,
			@JsonProperty(value = "batteryStatusValue", required = true) int batteryStatusValue,
			@JsonProperty(value = "chargingEquipmentStatusValue", required = true) int chargingEquipmentStatusValue,
			@JsonProperty(value = "dailyMaxInputVoltage", required = true) float dailyMaxInputVoltage, @JsonProperty(value = "dailyMinInputVoltage", required = true) float dailyMinInputVoltage,
			@JsonProperty(value = "dailyMaxBatteryVoltage", required = true) float dailyMaxBatteryVoltage, @JsonProperty(value = "dailyMinBatteryVoltage", required = true) float dailyMinBatteryVoltage,
			@JsonProperty(value = "dailyKWHConsumption", required = true) float dailyKWHConsumption, @JsonProperty(value = "monthlyKWHConsumption", required = true) float monthlyKWHConsumption,
			@JsonProperty(value = "yearlyKWHConsumption", required = true) float yearlyKWHConsumption, @JsonProperty(value = "cumulativeKWHConsumption", required = true) float cumulativeKWHConsumption,
			@JsonProperty(value = "dailyKWH", required = true) float dailyKWH, @JsonProperty(value = "monthlyKWH", required = true) float monthlyKWH,
			@JsonProperty(value = "yearlyKWH", required = true) float yearlyKWH, @JsonProperty(value = "cumulativeKWH", required = true) float cumulativeKWH,
			@JsonProperty(value = "carbonDioxideReductionTons", required = true) float carbonDioxideReductionTons,
			@JsonProperty(value = "netBatteryCurrent", required = true) float netBatteryCurrent,
			@JsonProperty(value = "batteryTemperatureCelsius331D", required = true) float batteryTemperatureCelsius331D,
			@JsonProperty(value = "ambientTemperatureCelsius", required = true) float ambientTemperatureCelsius,
			@JsonProperty(value = "batteryTypeValue", required = true) int batteryTypeValue,
			@JsonProperty(value = "batteryCapacityAmpHours", required = true) int batteryCapacityAmpHours,
			@JsonProperty(value = "temperatureCompensationCoefficient", required = true) int temperatureCompensationCoefficient,
			@JsonProperty(value = "highVoltageDisconnect", required = true) float highVoltageDisconnect, @JsonProperty(value = "chargingLimitVoltage", required = true) float chargingLimitVoltage, @JsonProperty(value = "overVoltageReconnect", required = true) float overVoltageReconnect,
			@JsonProperty(value = "equalizationVoltage", required = true) float equalizationVoltage, @JsonProperty(value = "boostVoltage", required = true) float boostVoltage, @JsonProperty(value = "floatVoltage", required = true) float floatVoltage, @JsonProperty(value = "boostReconnectVoltage", required = true) float boostReconnectVoltage,
			@JsonProperty(value = "lowVoltageReconnect", required = true) float lowVoltageReconnect, @JsonProperty(value = "underVoltageRecover", required = true) float underVoltageRecover, @JsonProperty(value = "underVoltageWarning", required = true) float underVoltageWarning,
			@JsonProperty(value = "lowVoltageDisconnect", required = true) float lowVoltageDisconnect, @JsonProperty(value = "dischargingLimitVoltage", required = true) float dischargingLimitVoltage,
			@JsonProperty(value = "secondMinuteHourDayMonthYearRaw", required = true) long secondMinuteHourDayMonthYearRaw,
			@JsonProperty(value = "equalizationChargingCycleDays", required = true) int equalizationChargingCycleDays,
			@JsonProperty(value = "batteryTemperatureWarningUpperLimit", required = true) float batteryTemperatureWarningUpperLimit, @JsonProperty(value = "batteryTemperatureWarningLowerLimit", required = true) float batteryTemperatureWarningLowerLimit, @JsonProperty(value = "insideControllerTemperatureWarningUpperLimit", required = true) float insideControllerTemperatureWarningUpperLimit, @JsonProperty(value = "insideControllerTemperatureWarningUpperLimitRecover", required = true) float insideControllerTemperatureWarningUpperLimitRecover,
			@JsonProperty(value = "powerComponentTemperatureWarningUpperLimit", required = true) float powerComponentTemperatureWarningUpperLimit, @JsonProperty(value = "powerComponentTemperatureWarningUpperLimitRecover", required = true) float powerComponentTemperatureWarningUpperLimitRecover,
			@JsonProperty(value = "lineImpedance", required = true) float lineImpedance,
			@JsonProperty(value = "nightPVVoltageThreshold", required = true) float nightPVVoltageThreshold,
			@JsonProperty(value = "lightSignalStartupDelayTime", required = true) int lightSignalStartupDelayTime,
			@JsonProperty(value = "dayPVVoltageThreshold", required = true) float dayPVVoltageThreshold,
			@JsonProperty(value = "lightSignalTurnOffDelayTime", required = true) int lightSignalTurnOffDelayTime,
			@JsonProperty(value = "loadControlModeValue", required = true) int loadControlModeValue,
			@JsonProperty(value = "workingTimeLength1Raw", required = true) int workingTimeLength1Raw, @JsonProperty(value = "workingTimeLength2Raw", required = true) int workingTimeLength2Raw,
			@JsonProperty(value = "turnOnTiming1Raw", required = true) long turnOnTiming1Raw, @JsonProperty(value = "turnOffTiming1Raw", required = true) long turnOffTiming1Raw, @JsonProperty(value = "turnOnTiming2Raw", required = true) long turnOnTiming2Raw, @JsonProperty(value = "turnOffTiming2Raw", required = true) long turnOffTiming2Raw,
			@JsonProperty(value = "lengthOfNightRaw", required = true) int lengthOfNightRaw,
			@JsonProperty(value = "batteryRatedVoltageCode", required = true) int batteryRatedVoltageCode,
			@JsonProperty(value = "loadTimingControlSelectionValue", required = true) int loadTimingControlSelectionValue,
			@JsonProperty(value = "isLoadOnByDefaultInManualMode", required = true) boolean isLoadOnByDefaultInManualMode,
			@JsonProperty(value = "equalizeDurationMinutes", required = true) int equalizeDurationMinutes, @JsonProperty(value = "boostDurationMinutes", required = true) int boostDurationMinutes,
			@JsonProperty(value = "dischargingPercentage", required = true) int dischargingPercentage, @JsonProperty(value = "chargingPercentage", required = true) int chargingPercentage,
			@JsonProperty(value = "batteryManagementModeValue", required = true) int batteryManagementModeValue,
			@JsonProperty(value = "isManualLoadControlOn", required = true) boolean isManualLoadControlOn, @JsonProperty(value = "isLoadTestModeEnabled", required = true) boolean isLoadTestModeEnabled, @JsonProperty(value = "isLoadForcedOn", required = true) boolean isLoadForcedOn,
			@JsonProperty(value = "isInsideControllerOverTemperature", required = true) boolean isInsideControllerOverTemperature, @JsonProperty(value = "isNight", required = true) boolean isNight) {
		this.packetVersion = packetVersion;
		identifier = TracerIdentifier.getFromNumber(number == null ? NumberedIdentifier.DEFAULT_NUMBER : number);
		identityInfo = new TracerIdentityInfo(ratedOutputCurrent);

		this.ratedInputVoltage = ratedInputVoltage;
		this.ratedInputCurrent = ratedInputCurrent;
		this.ratedInputPower = ratedInputPower;
		this.ratedOutputVoltage = ratedOutputVoltage;
		this.ratedOutputCurrent = ratedOutputCurrent;
		this.ratedOutputPower = ratedOutputPower;
		this.chargingTypeValue = chargingTypeValue;
		this.ratedLoadOutputCurrent = ratedLoadOutputCurrent;
		this.inputVoltage = inputVoltage;
		this.pvCurrent = pvCurrent;
		this.pvWattage = pvWattage;
		this.batteryVoltage = batteryVoltage;
		this.chargingCurrent = chargingCurrent;
		this.chargingPower = chargingPower;
		this.loadVoltage = loadVoltage;
		this.loadCurrent = loadCurrent;
		this.loadPower = loadPower;
		this.batteryTemperatureCelsius = batteryTemperatureCelsius;
		this.insideControllerTemperatureCelsius = insideControllerTemperatureCelsius;
		this.powerComponentTemperatureCelsius = powerComponentTemperatureCelsius;
		this.batterySOC = batterySOC;
		this.remoteBatteryTemperatureCelsius = remoteBatteryTemperatureCelsius;
		this.realBatteryRatedVoltageValue = realBatteryRatedVoltageValue;
		this.batteryStatusValue = batteryStatusValue;
		this.chargingEquipmentStatusValue = chargingEquipmentStatusValue;
		this.dailyMaxInputVoltage = dailyMaxInputVoltage;
		this.dailyMinInputVoltage = dailyMinInputVoltage;
		this.dailyMaxBatteryVoltage = dailyMaxBatteryVoltage;
		this.dailyMinBatteryVoltage = dailyMinBatteryVoltage;
		this.dailyKWHConsumption = dailyKWHConsumption;
		this.monthlyKWHConsumption = monthlyKWHConsumption;
		this.yearlyKWHConsumption = yearlyKWHConsumption;
		this.cumulativeKWHConsumption = cumulativeKWHConsumption;
		this.dailyKWH = dailyKWH;
		this.monthlyKWH = monthlyKWH;
		this.yearlyKWH = yearlyKWH;
		this.cumulativeKWH = cumulativeKWH;
		this.carbonDioxideReductionTons = carbonDioxideReductionTons;
		this.netBatteryCurrent = netBatteryCurrent;
		this.batteryTemperatureCelsius331D = batteryTemperatureCelsius331D;
		this.ambientTemperatureCelsius = ambientTemperatureCelsius;
		this.batteryTypeValue = batteryTypeValue;
		this.batteryCapacityAmpHours = batteryCapacityAmpHours;
		this.temperatureCompensationCoefficient = temperatureCompensationCoefficient;
		this.highVoltageDisconnect = highVoltageDisconnect;
		this.chargingLimitVoltage = chargingLimitVoltage;
		this.overVoltageReconnect = overVoltageReconnect;
		this.equalizationVoltage = equalizationVoltage;
		this.boostVoltage = boostVoltage;
		this.floatVoltage = floatVoltage;
		this.boostReconnectVoltage = boostReconnectVoltage;
		this.lowVoltageReconnect = lowVoltageReconnect;
		this.underVoltageRecover = underVoltageRecover;
		this.underVoltageWarning = underVoltageWarning;
		this.lowVoltageDisconnect = lowVoltageDisconnect;
		this.dischargingLimitVoltage = dischargingLimitVoltage;
		this.secondMinuteHourDayMonthYearRaw = secondMinuteHourDayMonthYearRaw;
		this.equalizationChargingCycleDays = equalizationChargingCycleDays;
		this.batteryTemperatureWarningUpperLimit = batteryTemperatureWarningUpperLimit;
		this.batteryTemperatureWarningLowerLimit = batteryTemperatureWarningLowerLimit;
		this.insideControllerTemperatureWarningUpperLimit = insideControllerTemperatureWarningUpperLimit;
		this.insideControllerTemperatureWarningUpperLimitRecover = insideControllerTemperatureWarningUpperLimitRecover;
		this.powerComponentTemperatureWarningUpperLimit = powerComponentTemperatureWarningUpperLimit;
		this.powerComponentTemperatureWarningUpperLimitRecover = powerComponentTemperatureWarningUpperLimitRecover;
		this.lineImpedance = lineImpedance;
		this.nightPVVoltageThreshold = nightPVVoltageThreshold;
		this.lightSignalStartupDelayTime = lightSignalStartupDelayTime;
		this.dayPVVoltageThreshold = dayPVVoltageThreshold;
		this.lightSignalTurnOffDelayTime = lightSignalTurnOffDelayTime;
		this.loadControlModeValue = loadControlModeValue;
		this.workingTimeLength1Raw = workingTimeLength1Raw;
		this.workingTimeLength2Raw = workingTimeLength2Raw;
		this.turnOnTiming1Raw = turnOnTiming1Raw;
		this.turnOffTiming1Raw = turnOffTiming1Raw;
		this.turnOnTiming2Raw = turnOnTiming2Raw;
		this.turnOffTiming2Raw = turnOffTiming2Raw;
		this.lengthOfNightRaw = lengthOfNightRaw;
		this.batteryRatedVoltageCode = batteryRatedVoltageCode;
		this.loadTimingControlSelectionValue = loadTimingControlSelectionValue;
		this.isLoadOnByDefaultInManualMode = isLoadOnByDefaultInManualMode;
		this.equalizeDurationMinutes = equalizeDurationMinutes;
		this.boostDurationMinutes = boostDurationMinutes;
		this.dischargingPercentage = dischargingPercentage;
		this.chargingPercentage = chargingPercentage;
		this.batteryManagementModeValue = batteryManagementModeValue;
		this.isManualLoadControlOn = isManualLoadControlOn;
		this.isLoadTestModeEnabled = isLoadTestModeEnabled;
		this.isLoadForcedOn = isLoadForcedOn;
		this.isInsideControllerOverTemperature = isInsideControllerOverTemperature;
		this.isNight = isNight;
	}

	@Override
	public @Nullable Integer getPacketVersion() {
		return packetVersion;
	}
	@Override public @NotNull TracerIdentifier getIdentifier() { return identifier; }
	@Override public @NotNull IdentityInfo getIdentityInfo() { return identityInfo; }
	@Override
	public int getNumber() {
		return identifier.getNumber();
	}

	@Override public int getRatedInputVoltage() { return ratedInputVoltage; }
	@Override public int getRatedInputCurrent() { return ratedInputCurrent; }
	@Override public int getRatedInputPower() { return ratedInputPower; }
	@Override public int getRatedOutputVoltage() { return ratedOutputVoltage; }
	@Override public int getRatedOutputCurrent() { return ratedOutputCurrent; }
	@Override public int getRatedOutputPower() { return ratedOutputPower; }
	@Override public int getChargingTypeValue() { return chargingTypeValue; }
	@Override public int getRatedLoadOutputCurrent() { return ratedLoadOutputCurrent; }
	@Override public @NotNull Float getPVVoltage() { return inputVoltage; }
	@Override public @NotNull Float getPVCurrent() { return pvCurrent; }
	@Override public @NotNull Float getPVWattage() { return pvWattage; }

	@Override public float getBatteryVoltage() { return batteryVoltage; }
	@Override public @NotNull Float getChargingCurrent() { return chargingCurrent; }
	@Override public @NotNull Float getChargingPower() { return chargingPower; }

	@Override public float getLoadVoltage() { return loadVoltage; }
	@Override public float getLoadCurrent() { return loadCurrent; }
	@Override public float getLoadPower() { return loadPower; }

	@Override public @NotNull Float getBatteryTemperatureCelsius() { return batteryTemperatureCelsius; }
	@Override public float getInsideControllerTemperatureCelsius() { return insideControllerTemperatureCelsius; }
	@Override public float getPowerComponentTemperatureCelsius() { return powerComponentTemperatureCelsius; }
	@Override public int getBatterySOC() { return batterySOC; }
	@Override public float getRemoteBatteryTemperatureCelsius() { return remoteBatteryTemperatureCelsius; }
	@Override public int getRealBatteryRatedVoltageValue() { return realBatteryRatedVoltageValue; }
	@Override public int getBatteryStatusValue() { return batteryStatusValue; }
	@Override public int getChargingEquipmentStatus() { return chargingEquipmentStatusValue; }

	@Override public float getDailyMaxPVVoltage() { return dailyMaxInputVoltage; }
	@Override public float getDailyMinPVVoltage() { return dailyMinInputVoltage; }
	@Override public float getDailyMaxBatteryVoltage() { return dailyMaxBatteryVoltage; }
	@Override public float getDailyMinBatteryVoltage() { return dailyMinBatteryVoltage; }

	@Override public float getDailyKWHConsumption() { return dailyKWHConsumption; }
	@Override public float getMonthlyKWHConsumption() { return monthlyKWHConsumption; }
	@Override public float getYearlyKWHConsumption() { return yearlyKWHConsumption; }
	@Override public float getCumulativeKWHConsumption() { return cumulativeKWHConsumption; }

	@Override public float getDailyKWH() { return dailyKWH; }
	@Override public float getMonthlyKWH() { return monthlyKWH; }
	@Override public float getYearlyKWH() { return yearlyKWH; }
	@Override public float getCumulativeKWH() { return cumulativeKWH; }
	@Override public float getCarbonDioxideReductionTons() { return carbonDioxideReductionTons; }
	@Override public float getNetBatteryCurrent() { return netBatteryCurrent; }
	@Override public float getBatteryTemperatureCelsius331D() { return batteryTemperatureCelsius331D; }
	@Override public float getAmbientTemperatureCelsius() { return ambientTemperatureCelsius; }
	@Override public int getBatteryTypeValue() { return batteryTypeValue; }
	@Override public int getBatteryCapacityAmpHours() { return batteryCapacityAmpHours; }
	@Override public int getTemperatureCompensationCoefficient() { return temperatureCompensationCoefficient; }

	@Override public float getHighVoltageDisconnect() { return highVoltageDisconnect; }
	@Override public float getChargingLimitVoltage() { return chargingLimitVoltage; }
	@Override public float getOverVoltageReconnect() { return overVoltageReconnect; }
	@Override public float getEqualizationVoltage() { return equalizationVoltage; }
	@Override public float getBoostVoltage() { return boostVoltage; }
	@Override public float getFloatVoltage() { return floatVoltage; }
	@Override public float getBoostReconnectVoltage() { return boostReconnectVoltage; }
	@Override public float getLowVoltageReconnect() { return lowVoltageReconnect; }
	@Override public float getUnderVoltageRecover() { return underVoltageRecover; }
	@Override public float getUnderVoltageWarning() { return underVoltageWarning; }
	@Override public float getLowVoltageDisconnect() { return lowVoltageDisconnect; }
	@Override public float getDischargingLimitVoltage() { return dischargingLimitVoltage; }
	@Override public long getSecondMinuteHourDayMonthYearRaw() { return secondMinuteHourDayMonthYearRaw; }
	@Override public int getEqualizationChargingCycleDays() { return equalizationChargingCycleDays; }
	@Override public float getBatteryTemperatureWarningUpperLimit() { return batteryTemperatureWarningUpperLimit; }
	@Override public float getBatteryTemperatureWarningLowerLimit() { return batteryTemperatureWarningLowerLimit; }
	@Override public float getInsideControllerTemperatureWarningUpperLimit() { return insideControllerTemperatureWarningUpperLimit; }
	@Override public float getInsideControllerTemperatureWarningUpperLimitRecover() { return insideControllerTemperatureWarningUpperLimitRecover; }
	@Override public float getPowerComponentTemperatureUpperLimit() { return powerComponentTemperatureWarningUpperLimit; }
	@Override public float getPowerComponentTemperatureUpperLimitRecover() { return powerComponentTemperatureWarningUpperLimitRecover; }
	@Override public float getLineImpedance() { return lineImpedance; }
	@Override public float getNightPVVoltageThreshold() { return nightPVVoltageThreshold; }
	@Override public int getLightSignalStartupDelayTime() { return lightSignalStartupDelayTime; }
	@Override public float getDayPVVoltageThreshold() { return dayPVVoltageThreshold; }
	@Override public int getLightSignalTurnOffDelayTime() { return lightSignalTurnOffDelayTime; }
	@Override public int getLoadControlModeValue() { return loadControlModeValue; }
	@Override public int getWorkingTimeLength1Raw() { return workingTimeLength1Raw; }
	@Override public int getWorkingTimeLength2Raw() { return workingTimeLength2Raw; }
	@Override public long getTurnOnTiming1Raw() { return turnOnTiming1Raw; }
	@Override public long getTurnOffTiming1Raw() { return turnOffTiming1Raw; }
	@Override public long getTurnOnTiming2Raw() { return turnOnTiming2Raw; }
	@Override public long getTurnOffTiming2Raw() { return turnOffTiming2Raw; }
	@Override public int getLengthOfNightRaw() { return lengthOfNightRaw; }
	@Override public int getBatteryRatedVoltageCode() { return batteryRatedVoltageCode; }
	@Override public int getLoadTimingControlSelectionValue() { return loadTimingControlSelectionValue; }
	@Override public boolean isLoadOnByDefaultInManualMode() { return isLoadOnByDefaultInManualMode; }
	// endregion
	@Override public int getEqualizeDurationMinutes() { return equalizeDurationMinutes; }
	@Override public int getBoostDurationMinutes() { return boostDurationMinutes; }
	@Override public int getDischargingPercentage() { return dischargingPercentage; }
	@Override public int getChargingPercentage() { return chargingPercentage; }
	@Override public int getBatteryManagementModeValue() { return batteryManagementModeValue; }
	@Override public boolean isManualLoadControlOn() { return isManualLoadControlOn; }
	@Override public boolean isLoadTestModeEnabled() { return isLoadTestModeEnabled; }
	@Override public boolean isLoadForcedOn() { return isLoadForcedOn; }
	@Override public boolean isInsideControllerOverTemperature() { return isInsideControllerOverTemperature; }
	@Override public boolean isNight() { return isNight; }
}
