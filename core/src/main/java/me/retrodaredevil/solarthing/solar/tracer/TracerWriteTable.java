package me.retrodaredevil.solarthing.solar.tracer;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.solar.tracer.mode.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;

public interface TracerWriteTable {
	@JsonProperty("batteryType")
	void setBatteryType(TracerBatteryType batteryType);
	@JsonProperty("batteryCapacityAmpHours")
	void setBatteryCapacityAmpHours(int batteryCapacityAmpHours);
	@JsonProperty("temperatureCompensationCoefficient")
	void setTemperatureCompensationCoefficient(int temperatureCompensationCoefficient);
	@JsonProperty("highVoltageDisconnect")
	void setHighVoltageDisconnect(float highVoltageDisconnect);
	@JsonProperty("chargingLimitVoltage")
	void setChargingLimitVoltage(float chargingLimitVoltage);
	@JsonProperty("overVoltageReconnect")
	void setOverVoltageReconnect(float overVoltageReconnect);
	@JsonProperty("equalizationVoltage")
	void setEqualizationVoltage(float equalizationVoltage);
	@JsonProperty("boostVoltage")
	void setBoostVoltage(float boostVoltage);
	@JsonProperty("floatVoltage")
	void setFloatVoltage(float floatVoltage);
	@JsonProperty("boostReconnectVoltage")
	void setBoostReconnectVoltage(float boostReconnectVoltage);
	@JsonProperty("lowVoltageReconnect")
	void setLowVoltageReconnect(float lowVoltageReconnect);
	@JsonProperty("underVoltageRecover")
	void setUnderVoltageRecover(float underVoltageRecover);
	@JsonProperty("underVoltageWarning")
	void setUnderVoltageWarning(float underVoltageWarning);
	@JsonProperty("lowVoltageDisconnect")
	void setLowVoltageDisconnect(float lowVoltageDisconnect);
	@JsonProperty("dischargingLimitVoltage")
	void setDischargingLimitVoltage(float dischargingLimitVoltage);
	void setSecondMinuteHourDayMonthYearRaw(long raw);
	default void setClock(int yearNumber, MonthDay monthDay, LocalTime time) {
		setSecondMinuteHourDayMonthYearRaw(TracerUtil.convertInstantToTracer48BitRaw(yearNumber, monthDay, time));
	}

	/**
	 * This is a non-standard method that represents the year 2021 as 21, 2022 as 22, etc.
	 */
	@JsonProperty("clockSolarThing")
	default void setSolarThingLocalDateTime(LocalDateTime localDateTime) {
		int newYear = localDateTime.getYear() - 2000;
		setClock(newYear, MonthDay.from(localDateTime), localDateTime.toLocalTime());
	}

	@JsonProperty("equalizationChargingCycleDays")
	void setEqualizationChargingCycleDays(int equalizationChargingCycleDays);
	@JsonProperty("batteryTemperatureWarningUpperLimit")
	void setBatteryTemperatureWarningUpperLimit(float batteryTemperatureWarningUpperLimit);
	@JsonProperty("batteryTemperatureWarningLowerLimit")
	void setBatteryTemperatureWarningLowerLimit(float batteryTemperatureWarningLowerLimit);
	@JsonProperty("insideControllerTemperatureWarningUpperLimit")
	void setInsideControllerTemperatureWarningUpperLimit(float insideControllerTemperatureWarningUpperLimit);
	@JsonProperty("insideControllerTemperatureWarningUpperLimitRecover")
	void setInsideControllerTemperatureWarningUpperLimitRecover(float insideControllerTemperatureWarningUpperLimitRecover);
	@JsonProperty("powerComponentTemperatureWarningUpperLimit")
	void setPowerComponentTemperatureUpperLimit(float powerComponentTemperatureUpperLimit);
	@JsonProperty("powerComponentTemperatureWarningUpperLimitRecover")
	void setPowerComponentTemperatureUpperLimitRecover(float powerComponentTemperatureUpperLimitRecover);
	@JsonProperty("lineImpedance")
	void setLineImpedance(float lineImpedance); // 0x901D // milliohms
	@JsonProperty("nightPVVoltageThreshold")
	void setNightPVVoltageThreshold(float nightPVVoltageThreshold);
	@JsonProperty("lightSignalStartupDelayTime")
	void setLightSignalStartupDelayTime(int lightSignalStartupDelayTime);
	@JsonProperty("dayPVVoltageThreshold")
	void setDayPVVoltageThreshold(float dayPVVoltageThreshold);
	@JsonProperty("lightSignalTurnOffDelayTime")
	void setLightSignalTurnOffDelayTime(int lightSignalTurnOffDelayTime);
	void setLoadControlMode(LoadControlMode loadControlMode);

	void setWorkingTimeLength1Raw(int workingTimeLength1Raw);
	default void setWorkingTime1Length(Duration workingTime1Length) { setWorkingTimeLength1Raw(TracerUtil.convertDurationToTracerDurationRaw(workingTime1Length)); }
	void setWorkingTimeLength2Raw(int workingTimeLength2Raw);
	default void setWorkingTime2Length(Duration workingTime2Length) { setWorkingTimeLength2Raw(TracerUtil.convertDurationToTracerDurationRaw(workingTime2Length)); }

	void setTurnOnTiming1Raw(long turnOnTiming1Raw);
	default void getTurnOnTiming1(LocalTime time) { setTurnOnTiming1Raw(TracerUtil.convertLocalTimeToTracer48BitRawTime(time)); }
	void setTurnOffTiming1Raw(long turnOffTiming1Raw);
	default void getTurnOffTiming1(LocalTime time) { setTurnOffTiming1Raw(TracerUtil.convertLocalTimeToTracer48BitRawTime(time)); }
	void setTurnOnTiming2Raw(long turnOnTiming2Raw);
	default void getTurnOnTiming2(LocalTime time) { setTurnOnTiming2Raw(TracerUtil.convertLocalTimeToTracer48BitRawTime(time)); }
	void setTurnOffTiming2Raw(long turnOffTiming2Raw);
	default void getTurnOffTiming2(LocalTime time) { setTurnOffTiming2Raw(TracerUtil.convertLocalTimeToTracer48BitRawTime(time)); }

	void setLengthOfNightRaw(int lengthOfNightRaw);
	default void setLengthOfNight(Duration lengthOfNight) { setLengthOfNightRaw(TracerUtil.convertDurationToTracerDurationRaw(lengthOfNight)); }

	/**
	 * Sets the battery rated voltage code
	 * @param batteryDetection The battery detection
	 */
	void setBatteryDetection(@NotNull BatteryDetection batteryDetection);
	void setLoadTimingControlSelection(@NotNull LoadTimingControlSelection loadTimingControlSelection);
	@JsonProperty("isLoadOnByDefaultInManualMode")
	void setLoadOnByDefaultInManualMode(boolean isLoadOnByDefaultInManualMode); // 0x906A
	@JsonProperty("equalizeDurationMinutes")
	void setEqualizeDurationMinutes(int equalizeDurationMinutes);
	@JsonProperty("boostDurationMinutes")
	void setBoostDurationMinutes(int boostDurationMinutes);
	@JsonProperty("dischargingPercentage")
	void setDischargingPercentage(int dischargingPercentage);
	@JsonProperty("chargingPercentage")
	void setChargingPercentage(int chargingPercentage);
	void setBatteryManagementMode(@NotNull BatteryManagementMode batteryManagementMode);

	@JsonProperty("isManualLoadControlOn")
	void setManualLoadControlOn(boolean isManualLoadControlOn);
	@JsonProperty("isLoadTestModeEnabled")
	void setLoadTestModeEnabled(boolean isLoadTestModeEnabled);
	@JsonProperty("isLoadForcedOn")
	void setLoadForcedOn(boolean isLoadForcedOn);

}
