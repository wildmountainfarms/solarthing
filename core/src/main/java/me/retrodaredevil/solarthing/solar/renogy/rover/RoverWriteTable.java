package me.retrodaredevil.solarthing.solar.renogy.rover;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.solar.renogy.BatteryType;
import me.retrodaredevil.solarthing.solar.renogy.Voltage;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.SpecialPowerControl_E021;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.SpecialPowerControl_E02D;

@SuppressWarnings("unused")
@JsonExplicit
public interface RoverWriteTable extends Rover {
	void factoryReset();
	void clearHistory();

	@JsonSetter("address")
	void setControllerDeviceAddress(int address);
	void setStreetLightStatus(StreetLight streetLightStatus);
	@JsonSetter("load")
	default void setLoadState(boolean on) {
		setStreetLightStatus(on ? StreetLight.ON : StreetLight.OFF);
	}
	@JsonSetter("streetLightBrightness")
	void setStreetLightBrightnessPercent(int brightnessPercent);
	default void setBatteryParameters(
			Voltage systemVoltage,
			BatteryType batteryType,
			int overVoltageThreshold,
			int chargingVoltageLimit,
			int equalizingChargingVoltage,
			int boostChargingVoltage,
			int floatingChargingVoltage,
			int boostChargingRecoveryVoltage,
			int overDischargeRecoveryVoltage,
			int underVoltageWarningLevel,
			int overDischargeVoltage,
			int dischargingLimitVoltage,
			int endOfChargeSOCValue, int endOfDischargeSOCValue,
			int overDischargeTimeDelaySeconds,
			int equalizingChargingTimeMinutes, // not raw
			int boostChargingTimeMinutes, // not raw
			int equalizingChargingIntervalDays, // not raw
			int temperatureCompensationFactor // not raw
	){
		setOverVoltageThresholdRaw(overVoltageThreshold);
		setChargingVoltageLimitRaw(chargingVoltageLimit);
		setEqualizingChargingVoltageRaw(equalizingChargingVoltage);
		setBoostChargingVoltageRaw(boostChargingVoltage);
		setFloatingChargingVoltageRaw(floatingChargingVoltage);
		setBoostChargingRecoveryVoltageRaw(boostChargingRecoveryVoltage);
		setOverDischargeRecoveryVoltageRaw(overDischargeRecoveryVoltage);
		setUnderVoltageWarningLevelRaw(underVoltageWarningLevel);
		setOverDischargeVoltageRaw(overDischargeVoltage);
		setDischargingLimitVoltageRaw(dischargingLimitVoltage);

		setEndOfChargeSOCEndOfDischargeSOC(endOfChargeSOCValue, endOfDischargeSOCValue);

		setOverDischargeTimeDelaySeconds(overDischargeTimeDelaySeconds);
		setEqualizingChargingTimeMinutes(equalizingChargingTimeMinutes);
		setBoostChargingTimeMinutes(boostChargingTimeMinutes);
		setEqualizingChargingIntervalDays(equalizingChargingIntervalDays);
		setTemperatureCompensationFactor(temperatureCompensationFactor);
	}

	void setSystemVoltageSetting(Voltage voltage);
	@JsonProperty("systemVoltageSetting")
	default void setSystemVoltageSettingValue(Integer voltageValue) {
		final Voltage voltage;
		if (voltageValue == null) {
			voltage = Voltage.AUTO;
		} else {
			voltage = Voltage.from(voltageValue);
		}
		setSystemVoltageSetting(voltage);
	}
	// I don't think we can set the recognized voltage
	@JsonProperty("batteryType")
	void setBatteryType(BatteryType batteryType);

	@JsonProperty("overVoltageThresholdRaw")
	void setOverVoltageThresholdRaw(int value);
	@JsonProperty("chargingVoltageLimitRaw")
	void setChargingVoltageLimitRaw(int value);
	@JsonProperty("equalizingChargingVoltageRaw")
	void setEqualizingChargingVoltageRaw(int value);
	@JsonProperty("boostChargingVoltageRaw")
	void setBoostChargingVoltageRaw(int value);
	@JsonProperty("floatingChargingVoltageRaw")
	void setFloatingChargingVoltageRaw(int value);
	@JsonProperty("boostChargingRecoveryVoltageRaw")
	void setBoostChargingRecoveryVoltageRaw(int value);
	@JsonProperty("overDischargeRecoveryVoltageRaw")
	void setOverDischargeRecoveryVoltageRaw(int value);
	@JsonProperty("underVoltageWarningLevelRaw")
	void setUnderVoltageWarningLevelRaw(int value);
	@JsonProperty("overDischargeVoltageRaw")
	void setOverDischargeVoltageRaw(int value);
	@JsonProperty("dischargingLimitVoltageRaw")
	void setDischargingLimitVoltageRaw(int value);

	void setEndOfChargeSOCEndOfDischargeSOC(int endOfChargeSOCValue, int endOfDischargeSOCValue);

	@JsonProperty("overDischargeTimeDelaySeconds")
	void setOverDischargeTimeDelaySeconds(int seconds);

	@JsonProperty("equalizingChargingTimeRaw")
	void setEqualizingChargingTimeRaw(int value);
	@JsonProperty("equalizingChargingTimeMinutes")
	default void setEqualizingChargingTimeMinutes(int minutes){
		setEqualizingChargingTimeRaw(minutes);
	}

	@JsonProperty("boostChargingTimeRaw")
	void setBoostChargingTimeRaw(int value);
	@JsonProperty("boostChargingTimeMinutes")
	default void setBoostChargingTimeMinutes(int minutes){
		setBoostChargingTimeRaw(minutes);
	}

	@JsonProperty("equalizingChargingIntervalRaw")
	void setEqualizingChargingIntervalRaw(int value);
	@JsonProperty("equalizingChargingIntervalDays")
	default void setEqualizingChargingIntervalDays(int days){
		setEqualizingChargingIntervalRaw(days);
	}

	/** NOTE: Untested */
	@JsonProperty("temperatureCompensationFactorRaw")
	void setTemperatureCompensationFactorRaw(int value);
	/** NOTE: Untested */
	@JsonProperty("temperatureCompensationFactor")
	default void setTemperatureCompensationFactor(int value){
		setTemperatureCompensationFactorRaw(value);
	}
	void setOperatingDurationHours(OperatingSetting setting, int hours);
	void setOperatingPowerPercentage(OperatingSetting setting, int operatingPowerPercentage);

	void setLoadWorkingMode(LoadWorkingMode loadWorkingMode);

	/**
	 * @param minutes A number in range [0..60]
	 */
	@JsonProperty("lightControlDelayMinutes")
	void setLightControlDelayMinutes(int minutes);

	/**
	 * @param voltage A number in range [1..40]
	 */
	@JsonProperty("lightControlVoltage")
	void setLightControlVoltage(int voltage);

	/**
	 * @param value Unknown range.
	 */
	@JsonProperty("ledLoadCurrentSettingRaw")
	void setLEDLoadCurrentSettingRaw(int value);
	@JsonProperty("ledLoadCurrentSettingMilliAmps")
	default void setLEDLoadCurrentSettingMilliAmps(int milliAmps){
		if(milliAmps % 10 != 0){
			throw new IllegalArgumentException("milliAmps must a multiple of 10! it was: " + milliAmps);
		}
		setLEDLoadCurrentSettingRaw(milliAmps / 10);
	}

	void setSpecialPowerControlE021Raw(int value);
	default void setSpecialPowerControl(SpecialPowerControl_E021 specialPowerControl){ setSpecialPowerControlE021Raw(specialPowerControl.getCombined()); }

	void setWorkingHoursRaw(Sensing sensing, int value);
	default void setWorkingHours(Sensing sensing, int hours){ setWorkingHoursRaw(sensing, hours - 1); }
	void setPowerWithPeopleSensedRaw(Sensing sensing, int value);
	default void setPowerWithPeopleSensedPercentage(Sensing sensing, int percentage) { setPowerWithPeopleSensedRaw(sensing, percentage - 10); }
	void setPowerWithNoPeopleSensedRaw(Sensing sensing, int value);
	default void setPowerWithNoPeopleSensedPercentage(Sensing sensing, int percentage) { setPowerWithNoPeopleSensedRaw(sensing, percentage - 10); }

	void setSensingTimeDelayRaw(int value);
	default void setSensingTimeDelaySeconds(int seconds){
		if(seconds < 10){
			throw new IllegalArgumentException("seconds cannot be less than 10! it was: " + seconds);
		}
		if(seconds > 260){
			throw new IllegalArgumentException("seconds cannot be greater than 260! it was: " + seconds);
		}
		setSensingTimeDelayRaw(seconds - 10);
	}

	void setLEDLoadCurrentRaw(int value);
	default void setLEDLoadCurrentMilliAmps(int milliAmps){
		if(milliAmps % 10 != 0){
			throw new IllegalArgumentException("milliAmps must a multiple of 10! it was: " + milliAmps);
		}
		setLEDLoadCurrentRaw(milliAmps / 10);
	}

	void setSpecialPowerControlE02DRaw(int value);
	default void setSpecialPowerControl(SpecialPowerControl_E02D specialPowerControl){ setSpecialPowerControlE02DRaw(specialPowerControl.getCombined()); }
}
