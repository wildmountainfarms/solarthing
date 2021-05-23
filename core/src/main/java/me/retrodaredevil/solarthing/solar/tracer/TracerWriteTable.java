package me.retrodaredevil.solarthing.solar.tracer;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.solar.tracer.mode.*;

import java.time.Duration;
import java.time.LocalTime;
import java.time.MonthDay;

public interface TracerWriteTable {
	void setBatteryType(TracerBatteryType batteryType);
	void setBatteryCapacityAmpHours(int batteryCapacityAmpHours);
	void setTemperatureCompensationCoefficient(int temperatureCompensationCoefficient);
	void setHighVoltageDisconnect(float highVoltageDisconnect);
	void setChargingLimitVoltage(float chargingLimitVoltage);
	void setOverVoltageReconnect(float overVoltageReconnect);
	void setEqualizationVoltage(float equalizationVoltage);
	void setBoostVoltage(float boostVoltage);
	void setFloatVoltage(float floatVoltage);
	void setBoostReconnectVoltage(float boostReconnectVoltage);
	void setLowVoltageReconnect(float lowVoltageReconnect);
	void setUnderVoltageRecover(float underVoltageRecover);
	void setUnderVoltageWarning(float underVoltageWarning);
	void setLowVoltageDisconnect(float lowVoltageDisconnect);
	void setDischargingLimitVoltage(float dischargingLimitVoltage);
	void setSecondMinuteHourDayMonthYearRaw(long raw);
	default void setClock(int yearNumber, MonthDay monthDay, LocalTime time) {
		setSecondMinuteHourDayMonthYearRaw(TracerUtil.convertInstantToTracer48BitRaw(yearNumber, monthDay, time));
	}

	void setEqualizationChargingCycleDays(int equalizationChargingCycleDays);
	void setBatteryTemperatureWarningUpperLimit(float batteryTemperatureWarningUpperLimit);
	void setBatteryTemperatureWarningLowerLimit(float batteryTemperatureWarningLowerLimit);
	void setInsideControllerTemperatureWarningUpperLimit(float insideControllerTemperatureWarningUpperLimit);
	void setInsideControllerTemperatureWarningUpperLimitRecover(float insideControllerTemperatureWarningUpperLimitRecover);
	void setPowerComponentTemperatureUpperLimit(float powerComponentTemperatureUpperLimit);
	void setPowerComponentTemperatureUpperLimitRecover(float powerComponentTemperatureUpperLimitRecover);
	void setLineImpedance(float lineImpedance); // 0x901D // milliohms
	void setNightPVVoltageThreshold(float nightPVVoltageThreshold);
	void setLightSignalStartupDelayTime(int lightSignalStartupDelayTime);
	void setDayPVVoltageThreshold(float dayPVVoltageThreshold);
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
	void setLoadOnByDefaultInManualMode(boolean isLoadOnByDefaultInManualMode); // 0x906A
	void setEqualizeDurationMinutes(int equalizeDurationMinutes);
	void setBoostDurationMinutes(int boostDurationMinutes);
	void setDischargingPercentage(int dischargingPercentage); // TODO we may change this if we decide to change the get interface too
	void setChargingPercentage(int chargingPercentage);
	void setBatteryManagementMode(@NotNull BatteryManagementMode batteryManagementMode);

	void setManualLoadControlOn(boolean isManualLoadControlOn);
	void setLoadTestModeEnabled(boolean isLoadTestModeEnabled);
	void setLoadForcedOn(boolean isLoadForcedOn);

}
