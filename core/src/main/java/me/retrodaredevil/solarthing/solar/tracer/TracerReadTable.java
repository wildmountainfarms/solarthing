package me.retrodaredevil.solarthing.solar.tracer;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.io.serial.SerialConfigBuilder;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.SerializeNameDefinedInBase;
import me.retrodaredevil.solarthing.packets.Mode;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.packets.support.Support;
import me.retrodaredevil.solarthing.solar.common.AdvancedAccumulatedChargeController;
import me.retrodaredevil.solarthing.solar.common.BasicChargeController;
import me.retrodaredevil.solarthing.solar.common.ErrorReporter;
import me.retrodaredevil.solarthing.solar.common.RecordBatteryVoltage;
import me.retrodaredevil.solarthing.solar.tracer.mode.*;

import java.time.Duration;
import java.time.LocalTime;
import java.time.MonthDay;
import java.util.Set;

@JsonExplicit
public interface TracerReadTable extends RecordBatteryVoltage, BasicChargeController, AdvancedAccumulatedChargeController, ErrorReporter {

	SerialConfig SERIAL_CONFIG = new SerialConfigBuilder(115200)
			.setDataBits(8)
			.setStopBits(SerialConfig.StopBits.ONE)
			.setParity(SerialConfig.Parity.NONE)
			.build();

	@Override
	default @NotNull ChargingStatus getChargingMode() {
		// TODO, if we figure out if there's a way to tell if the tracer is actually in one of these modes rather than just in Bulk, we may
		//   consider creating another enum representing that possibility
		return getChargingStatus();
	}

	@Override
	default int getDailyAH() { return 0; }
	@Override
	default @NotNull Support getDailyAHSupport() { return Support.NOT_SUPPORTED; }

	@JsonProperty("ratedInputVoltage")
	float getRatedInputVoltage();
	@JsonProperty("ratedInputCurrent")
	float getRatedInputCurrent();
	@JsonProperty("ratedInputPower")
	float getRatedInputPower();


	@JsonProperty("ratedOutputVoltage")
	float getRatedOutputVoltage(); // basically the nominal battery voltage (I think)
	@JsonProperty("ratedOutputCurrent")
	float getRatedOutputCurrent();
	@JsonProperty("ratedOutputPower")
	float getRatedOutputPower();

	@JsonProperty("chargingTypeValue")
	int getChargingTypeValue(); // 0x3008
	@GraphQLInclude("chargingType")
	default @NotNull TracerChargingType getChargingType() { return Modes.getActiveMode(TracerChargingType.class, getChargingTypeValue()); }

	@JsonProperty("ratedLoadOutputCurrent")
	float getRatedLoadOutputCurrent();

	// ===

	@SerializeNameDefinedInBase
	@Override
	@NotNull Float getPVVoltage(); // 0x3100
	@SerializeNameDefinedInBase
	@Override
	@NotNull Float getPVCurrent();

	@JsonProperty("pvWattage")
	@Override
	@NotNull Float getPVWattage();

	@SerializeNameDefinedInBase
	@Override
	float getBatteryVoltage(); // 0x3104

	@JsonProperty("chargingCurrent")
	@Override
	@NotNull Float getChargingCurrent();
	// Page 2
	@JsonProperty("chargingPower")
	@Override
	@NotNull Float getChargingPower();

	@JsonProperty("loadVoltage")
	float getLoadVoltage();
	@JsonProperty("loadCurrent")
	float getLoadCurrent();
	@JsonProperty("loadPower")
	float getLoadPower();

	@JsonProperty("batteryTemperatureCelsius")
	float getBatteryTemperatureCelsius(); // 0x3110

	/**
	 * @return The temperature inside the controller
	 */
	@JsonProperty("insideControllerTemperatureCelsius")
	float getInsideControllerTemperatureCelsius();
	/**
	 * In my experience, this has the same value as {@link #getInsideControllerTemperatureCelsius()}
	 */
	@JsonProperty("powerComponentTemperatureCelsius")
	float getPowerComponentTemperatureCelsius();

	/**
	 * Note: State of charge values are estimates and are usually never accurate
	 * @return A number in range [0..100] representing the SOC
	 */
	@JsonProperty("batterySOC")
	int getBatterySOC();

	/**
	 * On EPEver tracer, I always find this value is 0.0, so don't use this
	 */
	@JsonProperty("remoteBatteryTemperatureCelsius")
	float getRemoteBatteryTemperatureCelsius();

	@JsonProperty("realBatteryRatedVoltageValue")
	int getRealBatteryRatedVoltageValue();

	// ===

	@JsonProperty("batteryStatusValue")
	int getBatteryStatusValue();
	@JsonProperty("temp_getBatteryVoltageStatusValue")
	default int getBatteryVoltageStatusValue() { return getBatteryStatusValue() & 0b1111; }
	@JsonProperty("temp_getBatteryTemperatureStatusValue")
	default int getBatteryTemperatureStatusValue() { return getBatteryStatusValue() & 0b11110000; }
	@JsonProperty("temp_isBatteryInternalResistanceAbnormal")
	@GraphQLInclude("isBatteryInternalResistanceAbnormal")
	default boolean isBatteryInternalResistanceAbnormal() { return (getBatteryStatusValue() & 0b100000000) != 0; } // check bit8
	@JsonProperty("temp_isBatteryWrongIdentificationForRatedVoltage")
	@GraphQLInclude("isBatteryWrongIdentificationForRatedVoltage")
	default boolean isBatteryWrongIdentificationForRatedVoltage() { return ((getBatteryStatusValue() >> 15) & 1) != 0; } // check bit15
	@JsonProperty("temp_isBatteryWrongIdentificationForRatedVoltage")
	@GraphQLInclude("temp_getBatteryVoltageStatus")
	default @NotNull TracerBatteryVoltageStatus getBatteryVoltageStatus() { return Modes.getActiveMode(TracerBatteryVoltageStatus.class, getBatteryVoltageStatusValue()); }
	@JsonProperty("temp_getBatteryTemperatureStatus")
	@GraphQLInclude("batteryTemperatureStatus")
	default @NotNull TracerBatteryTemperatureStatus getBatteryTemperatureStatus() { return Modes.getActiveMode(TracerBatteryTemperatureStatus.class, getBatteryTemperatureStatusValue()); }

	@JsonProperty("chargingEquipmentStatusValue")
	int getChargingEquipmentStatus();
	@JsonProperty("temp_getInputVoltageStatusValue")
	default int getInputVoltageStatusValue() { return getChargingEquipmentStatus() >> 14; }
	@JsonProperty("temp_getChargingStatusValue")
	default int getChargingStatusValue() { return (getChargingEquipmentStatus() >> 2) & 0b11; }
	@JsonProperty("temp_getErrorModes")
	@Override
	default @NotNull Set<ChargingEquipmentError> getErrorModes() { return Modes.getActiveModes(ChargingEquipmentError.class, getChargingEquipmentStatus()); }
	/**
	 * @deprecated Use {@link #getChargingEquipmentStatus()} instead
	 * @return {@link #getChargingEquipmentStatus()}
	 */
	@Deprecated
	@Override
	default int getErrorModeValue() { return getChargingEquipmentStatus(); }
	@JsonProperty("temp_isRunning")
	@GraphQLInclude("isRunning")
	default boolean isRunning() { return (getChargingEquipmentStatus() & 1) == 1; }
	@JsonProperty("temp_getInputVoltageStatus")
	@GraphQLInclude("inputVoltageStatus")
	default @NotNull InputVoltageStatus getInputVoltageStatus() { return Modes.getActiveMode(InputVoltageStatus.class, getInputVoltageStatusValue()); }
	@JsonProperty("temp_getChargingStatus")
	@GraphQLInclude("chargingStatus")
	default @NotNull ChargingStatus getChargingStatus() { return Modes.getActiveMode(ChargingStatus.class, getChargingStatusValue()); }

	// Page 3
	// region Read Only Accumulators + Extra
	@JsonProperty("dailyMaxInputVoltage")
	float getDailyMaxPVVoltage();
	@JsonProperty("dailyMinInputVoltage")
	float getDailyMinPVVoltage();
	@SerializeNameDefinedInBase
	@Override
	float getDailyMaxBatteryVoltage(); // 0x3302
	@SerializeNameDefinedInBase
	@Override
	float getDailyMinBatteryVoltage();
	@SerializeNameDefinedInBase
	@Override
	float getDailyKWHConsumption();
	@JsonProperty("monthlyKWHConsumption")
	float getMonthlyKWHConsumption();
	@JsonProperty("yearlyKWHConsumption")
	float getYearlyKWHConsumption();
	@SerializeNameDefinedInBase
	@Override
	float getCumulativeKWHConsumption();
	@SerializeNameDefinedInBase
	@Override
	float getDailyKWH(); // 0x330C
	@JsonProperty("monthlyKWH")
	float getMonthlyKWH();
	@JsonProperty("yearlyKWH")
	float getYearlyKWH();
	@SerializeNameDefinedInBase
	@Override
	float getCumulativeKWH();
	@JsonProperty("carbonDioxideReductionTons")
	float getCarbonDioxideReductionTons();

	@JsonProperty("netBatteryCurrent")
	float getNetBatteryCurrent();

	/**
	 * In my experience, this has the same value as {@link #getAmbientTemperatureCelsius()}
	 */
	@JsonProperty("batteryTemperatureCelsius331D")
	float getBatteryTemperatureCelsius331D(); // 0x331D

	/**
	 *
	 * @return The temperature reading of the remote temperature sensor (the sensor plugged into the controller)
	 */
	@JsonProperty("ambientTemperatureCelsius")
	float getAmbientTemperatureCelsius();
	// endregion

	// region Read-write settings
	@JsonProperty("batteryTypeValue")
	int getBatteryTypeValue(); // 0x9000
	default @NotNull TracerBatteryType getBatteryType() { return Modes.getActiveMode(TracerBatteryType.class, getBatteryTypeValue()); }
	@JsonProperty("batteryCapacityAmpHours")
	int getBatteryCapacityAmpHours();
	@JsonProperty("temperatureCompensationCoefficient")
	int getTemperatureCompensationCoefficient();
	@JsonProperty("highVoltageDisconnect")
	float getHighVoltageDisconnect();
	@JsonProperty("chargingLimitVoltage")
	float getChargingLimitVoltage();
	@JsonProperty("overVoltageReconnect")
	float getOverVoltageReconnect();
	@JsonProperty("equalizationVoltage")
	float getEqualizationVoltage();
	@JsonProperty("boostVoltage")
	float getBoostVoltage();
	@JsonProperty("floatVoltage")
	float getFloatVoltage();
	@JsonProperty("boostReconnectVoltage")
	float getBoostReconnectVoltage();
	@JsonProperty("lowVoltageReconnect")
	float getLowVoltageReconnect();
	@JsonProperty("underVoltageRecover")
	float getUnderVoltageRecover();
	@JsonProperty("underVoltageWarning")
	float getUnderVoltageWarning();
	@JsonProperty("lowVoltageDisconnect")
	float getLowVoltageDisconnect();
	@JsonProperty("dischargingLimitVoltage")
	float getDischargingLimitVoltage();

	/** @return 48 bit number representing a real time clock. Low 8 bits represent seconds, ..., high 8 bits represent year */
	@JsonProperty("secondMinuteHourDayMonthYearRaw")
	long getSecondMinuteHourDayMonthYearRaw();
	@JsonProperty("temp_getClockTime")
	default LocalTime getClockTime() {
		return TracerUtil.convertTracer48BitRawTimeToLocalTime(getSecondMinuteHourDayMonthYearRaw());
	}
	@JsonProperty("temp_getClockMonthDay")
	default MonthDay getClockMonthDay() {
		return TracerUtil.extractTracer48BitRawInstantToMonthDay(getSecondMinuteHourDayMonthYearRaw());
	}
	@JsonProperty("temp_getClockYearNumber")
	default int getClockYearNumber() {
		return TracerUtil.extractTracer48BitRawInstantToYearNumber(getSecondMinuteHourDayMonthYearRaw());
	}

	@JsonProperty("equalizationChargingCycleDays")
	int getEqualizationChargingCycleDays();
	@JsonProperty("batteryTemperatureWarningUpperLimit")
	float getBatteryTemperatureWarningUpperLimit();
	@JsonProperty("batteryTemperatureWarningLowerLimit")
	float getBatteryTemperatureWarningLowerLimit();
	@JsonProperty("insideControllerTemperatureWarningUpperLimit")
	float getInsideControllerTemperatureWarningUpperLimit();
	@JsonProperty("insideControllerTemperatureWarningUpperLimitRecover")
	float getInsideControllerTemperatureWarningUpperLimitRecover();
	@JsonProperty("powerComponentTemperatureWarningUpperLimit")
	float getPowerComponentTemperatureUpperLimit();
	@JsonProperty("powerComponentTemperatureWarningUpperLimitRecover")
	float getPowerComponentTemperatureUpperLimitRecover();
	@JsonProperty("lineImpedance")
	float getLineImpedance(); // 0x901D // milliohms
	@JsonProperty("nightPVVoltageThreshold")
	float getNightPVVoltageThreshold();
	@JsonProperty("lightSignalStartupDelayTime")
	int getLightSignalStartupDelayTime();
	@JsonProperty("dayPVVoltageThreshold")
	float getDayPVVoltageThreshold();
	@JsonProperty("lightSignalTurnOffDelayTime")
	int getLightSignalTurnOffDelayTime();
	@JsonProperty("loadControlModeValue")
	int getLoadControlModeValue();
	default LoadControlMode getLoadControlMode() { return Modes.getActiveMode(LoadControlMode.class, getLoadControlModeValue()); }

	@JsonProperty("workingTimeLength1Raw")
	int getWorkingTimeLength1Raw();
	@JsonProperty("temp_getWorkingTime1Length")
	default Duration getWorkingTime1Length() { return TracerUtil.convertTracerDurationRawToDuration(getWorkingTimeLength1Raw()); }
	@JsonProperty("workingTimeLength2Raw")
	int getWorkingTimeLength2Raw();
	@JsonProperty("temp_getWorkingTime2Length")
	default Duration getWorkingTime2Length() { return TracerUtil.convertTracerDurationRawToDuration(getWorkingTimeLength2Raw()); }

	@JsonProperty("turnOnTiming1Raw")
	long getTurnOnTiming1Raw();
	default LocalTime getTurnOnTiming1() { return TracerUtil.convertTracer48BitRawTimeToLocalTime(getTurnOnTiming1Raw()); }
	@JsonProperty("turnOffTiming1Raw")
	long getTurnOffTiming1Raw();
	default LocalTime getTurnOffTiming1() { return TracerUtil.convertTracer48BitRawTimeToLocalTime(getTurnOffTiming1Raw()); }
	@JsonProperty("turnOnTiming2Raw")
	long getTurnOnTiming2Raw();
	default LocalTime getTurnOnTiming2() { return TracerUtil.convertTracer48BitRawTimeToLocalTime(getTurnOnTiming2Raw()); }
	@JsonProperty("turnOffTiming2Raw")
	long getTurnOffTiming2Raw();
	default LocalTime getTurnOffTiming2() { return TracerUtil.convertTracer48BitRawTimeToLocalTime(getTurnOffTiming2Raw()); }
	@JsonProperty("lengthOfNightRaw")
	int getLengthOfNightRaw();
	default Duration getLengthOfNight() { return TracerUtil.convertTracerDurationRawToDuration(getLengthOfNightRaw()); }

	@JsonProperty("batteryRatedVoltageCode")
	int getBatteryRatedVoltageCode();
	@GraphQLInclude("batteryDetection")
	default @NotNull BatteryDetection getBatteryDetection() { return Modes.getActiveMode(BatteryDetection.class, getBatteryRatedVoltageCode()); }
	@JsonProperty("loadTimingControlSelectionValueRaw")
	int getLoadTimingControlSelectionValue();
	@GraphQLInclude("loadTimingControlSelection")
	default @NotNull LoadTimingControlSelection getLoadTimingControlSelection() { return Modes.getActiveMode(LoadTimingControlSelection.class, getLoadTimingControlSelectionValue()); }
	@JsonProperty("isLoadOnByDefaultInManualMode")
	boolean isLoadOnByDefaultInManualMode(); // 0x906A
	@JsonProperty("equalizeDurationMinutes")
	int getEqualizeDurationMinutes();
	@JsonProperty("boostDurationMinutes")
	int getBoostDurationMinutes();
	@JsonProperty("dischargingPercentage")
	int getDischargingPercentage();
	@JsonProperty("chargingPercentage")
	int getChargingPercentage();
	@JsonProperty("batteryManagementModeValue")
	int getBatteryManagementModeValue();
	@GraphQLInclude("batteryManagementMode")
	default @NotNull BatteryManagementMode getBatteryManagementMode() { return Modes.getActiveMode(BatteryManagementMode.class, getBatteryManagementModeValue()); }

	@JsonProperty("isManualLoadControlOn")
	boolean isManualLoadControlOn();
	@JsonProperty("isLoadTestModeEnabled")
	boolean isLoadTestModeEnabled();
	@JsonProperty("isLoadForcedOn")
	boolean isLoadForcedOn();

	// endregion

	@JsonProperty("isInsideControllerOverTemperature")
	boolean isInsideControllerOverTemperature();
	@JsonProperty("isNight")
	boolean isNight();

}
