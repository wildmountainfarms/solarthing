package me.retrodaredevil.solarthing.solar.tracer;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.SerializeNameDefinedInBase;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.common.AdvancedAccumulatedChargeController;
import me.retrodaredevil.solarthing.solar.common.BasicChargeController;
import me.retrodaredevil.solarthing.solar.common.ErrorReporter;
import me.retrodaredevil.solarthing.solar.common.RecordBatteryVoltage;
import me.retrodaredevil.solarthing.solar.tracer.mode.*;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Set;

@JsonExplicit
public interface TracerReadTable extends RecordBatteryVoltage, BasicChargeController, AdvancedAccumulatedChargeController, ErrorReporter {

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

	@JsonProperty("chargingModeValue")
	int getChargingModeValue(); // 0x3008
	@Deprecated
	@Override // TODO, we might want to rename this method and use getChargingStatus() instead, or even come up with our own charging mode enum
	default @NotNull TracerChargingMode getChargingMode() { return Modes.getActiveMode(TracerChargingMode.class, getChargingModeValue()); }

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
	@JsonProperty("insideControllerTemperatureCelsius")
	float getInsideControllerTemperatureCelsius();
	@JsonProperty("powerComponentTemperatureCelsius")
	float getPowerComponentTemperatureCelsius();

	@JsonProperty("batterySOC")
	int getBatterySOC(); // TODO Is the raw range of this 0 to 100? Or 0 to (100 * 100)? If 0 to (100 * 100), then we should make this a double, maybe make it a double anyway

	@JsonProperty("remoteBatteryTemperatureCelsius")
	float getRemoteBatteryTemperatureCelsius();

	@JsonProperty("realBatteryRatedVoltageValue")
	int getRealBatteryRatedVoltageValue();

	// ===

	@JsonProperty("batteryStatusValue")
	int getBatteryStatusValue();
	default int getBatteryVoltageStatusValue() { return getBatteryStatusValue() & 0b1111; }
	default int getBatteryTemperatureStatusValue() { return getBatteryStatusValue() & 0b11110000; }
	default boolean isBatteryInternalResistanceAbnormal() { return (getBatteryStatusValue() & 0b100000000) != 0; } // check bit8
	default boolean isBatteryWrongIdentificationForRatedVoltage() { return ((getBatteryStatusValue() >> 15) & 1) != 0; } // check bit15
	default @NotNull TracerBatteryVoltageStatus getBatteryVoltageStatus() { return Modes.getActiveMode(TracerBatteryVoltageStatus.class, getBatteryVoltageStatusValue()); }
	default @NotNull TracerBatteryTemperatureStatus getBatteryTemperatureStatus() { return Modes.getActiveMode(TracerBatteryTemperatureStatus.class, getBatteryTemperatureStatusValue()); }

	@JsonProperty("chargingEquipmentStatusValue")
	int getChargingEquipmentStatus();
	default int getInputVoltageStatusValue() { return getChargingEquipmentStatus() >> 14; }
	default int getChargingStatusValue() { return (getChargingEquipmentStatus() >> 2) & 0b11; }
	@Override
	default @NotNull Set<ChargingEquipmentError> getErrorModes() { return Modes.getActiveModes(ChargingEquipmentError.class, getChargingEquipmentStatus()); }
	default boolean isRunning() { return (getChargingEquipmentStatus() & 1) == 1; }
	default @NotNull InputVoltageStatus getInputVoltageStatus() { return Modes.getActiveMode(InputVoltageStatus.class, getInputVoltageStatusValue()); }
	default @NotNull ChargingStatus getChargingStatus() { return Modes.getActiveMode(ChargingStatus.class, getChargingStatusValue()); }

	// Page 3
	// region Read Only Accumulators + Extra
	@JsonProperty("dailyMaxInputVoltage")
	float getDailyMaxInputVoltage();
	@JsonProperty("dailyMinInputVoltage")
	float getDailyMinInputVoltage();
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
	@Override
	float getCumulativeKWHConsumption();
	@Override
	float getDailyKWH(); // 0x330C
	float getMonthlyKWH();
	float getYearlyKWH();
	@Override
	float getCumulativeKWH();
	float getCarbonDioxideReductionTons();

	float getNetBatteryCurrent();
	float getBatteryTemperatureCelsius331D(); // 0x331D
	float getAmbientTemperatureCelsius();
	// endregion

	// region Read-write settings
	int getBatteryTypeValue(); // 0x9000
	int getBatteryCapacityAmpHours();
	int getTemperatureCompensationCoefficient();
	float getHighVoltageDisconnect();
	float getChargingLimitVoltage();
	float getOverVoltageReconnect();
	float getEqualizationVoltage();
	float getBoostVoltage();
	float getFloatVoltage();
	float getBoostReconnectVoltage();
	float getLowVoltageReconnect();
	float getUnderVoltageRecover();
	float getUnderVoltageWarning();
	float getLowVoltageDisconnect();
	float getDischargingLimitVoltage();

	// Minutes and Seconds on 0x9013

	/** @return 16 bit number representing seconds and minutes. Low 8 bits are seconds, high 8 bits are minutes */
	int getSecondsMinutesRaw();
	/** @return 16 bit number representing hour and day. Low 8 bits are hour, high 8 bits are day */
	int getHourDayRaw();
	/** @return 16 bit number representing month and year. Low 8 bits are month, high 8 bits are year */
	int getMonthYearRaw();

	int getEqualizationChargingCycleDays();
	float getBatteryTemperatureWarningUpperLimit();
	float getBatteryTemperatureWarningLowerLimit();
	float getInsideControllerTemperatureWarningUpperLimit();
	float getInsideControllerTemperatureWarningUpperLimitRecover();
	float getPowerComponentTemperatureUpperLimit();
	float getPowerComponentTemperatureUpperLimitRecover();
	float getLineImpedance(); // 0x901D // milliohms
	float getNightPVVoltageThreshold();
	int getLightSignalStartupDelayTime();
	float getDayPVVoltageThreshold();
	int getLightSignalTurnOffDelayTime();
	int getLoadControlModeValue();
	default LoadControlMode getLoadControlMode() { return Modes.getActiveMode(LoadControlMode.class, getLoadControlModeValue()); }

	int getWorkingTimeLength1Raw();
	default Duration getWorkingTime1Length() { return TracerUtil.convertTracerDurationRawToDuration(getWorkingTimeLength1Raw()); }
	int getWorkingTimeLength2Raw();
	default Duration getWorkingTime2Length() { return TracerUtil.convertTracerDurationRawToDuration(getWorkingTimeLength2Raw()); }

	int getTurnOnTiming1Raw();
	default LocalTime getTurnOnTiming1() { return TracerUtil.convertTracerRawTimeToLocalTime(getTurnOnTiming1Raw()); }
	int getTurnOffTiming1Raw();
	default LocalTime getTurnOffTiming1() { return TracerUtil.convertTracerRawTimeToLocalTime(getTurnOffTiming1Raw()); }
	int getTurnOnTiming2Raw();
	default LocalTime getTurnOnTiming2() { return TracerUtil.convertTracerRawTimeToLocalTime(getTurnOnTiming2Raw()); }
	int getTurnOffTiming2Raw();
	default LocalTime getTurnOffTiming2() { return TracerUtil.convertTracerRawTimeToLocalTime(getTurnOffTiming2Raw()); }
	int getLengthOfNightRaw();
	default Duration getLengthOfNight() { return TracerUtil.convertTracerDurationRawToDuration(getLengthOfNightRaw()); }

	int getBatteryRatedVoltageCode();
	default @NotNull BatteryDetection getBatteryDetection() { return Modes.getActiveMode(BatteryDetection.class, getBatteryRatedVoltageCode()); }
	int getLoadTimingControlSelectionValueRaw();
	boolean isLoadOnByDefaultInManualMode(); // 0x906A
	int getEqualizeDurationMinutes();
	int getBoostDurationMinutes();
	int getDischargingPercentage();
	int getChargingPercentage();
	@JsonProperty("batteryManagementModeValue")
	int getBatteryManagementModeValue();
	default @NotNull BatteryManagementMode getBatteryManagementMode() { return Modes.getActiveMode(BatteryManagementMode.class, getBatteryManagementModeValue()); }

	boolean isManualLoadControlOn();
	boolean isLoadTestModeEnabled();
	boolean isLoadForcedOn();
	boolean isInsideControllerOverTemperature();
	boolean isNight();

	// endregion

}
