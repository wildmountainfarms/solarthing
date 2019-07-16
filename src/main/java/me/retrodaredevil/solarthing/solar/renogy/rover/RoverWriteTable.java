package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.solar.renogy.BatteryType;
import me.retrodaredevil.solarthing.solar.renogy.Voltage;

@SuppressWarnings("unused")
public interface RoverWriteTable {
	void setControllerDeviceAddress(int address);
	void setStreetLightStatus(StreetLight streetLightStatus);
	void setStreetLightBrightnessPercent(int brightnessPercent);
	void setSystemVoltageSetting(Voltage voltage);
	// I don't think we can set the recognized voltage
	void setBatteryType(BatteryType batteryType);
	void setOverVoltageThresholdRaw(int value);
	void setChargingVoltageLimitRaw(int value);
	void setEqualizingChargingVoltageRaw(int value);
	void setBoostChargingVoltageRaw(int value);
	void setFloatingChargingVoltageRaw(int value);
	void setBoostChargingRecoveryVoltageRaw(int value);
	void setOverDischargeRecoveryVoltageRaw(int value);
	void setUnderVoltageWarningLevelRaw(int value);
	void setOverDischargeVoltageRaw(int value);
	void setDischargingLimitVoltageRaw(int value);
	
	void setEndOfChargeSOCEndOfDischargeSOC(int endOfChargeSOCValue, int endOfDischargeSOCValue);
	
	void setOverDischargeTimeDelaySeconds(int seconds);
	
	void setEqualizingChargingTimeRaw(int value);
	default void setEqualizingChargingTimeMinutes(int minutes){
		if(minutes < 10){
			throw new IllegalArgumentException("minutes cannot be less than 10! it was: " + minutes);
		}
		if(minutes > 310){
			throw new IllegalArgumentException("minutes cannot be greater than 310! it was: " + minutes);
		}
		setEqualizingChargingTimeRaw(minutes - 10);
	}
	
	void setBoostChargingTimeRaw(int value);
	default void setBoostChargingTimeMinutes(int minutes){
		if(minutes < 20){
			throw new IllegalArgumentException("minutes cannot be less than 20! it was: " + minutes);
		}
		if(minutes > 310){
			throw new IllegalArgumentException("minutes cannot be greater than 310! it was: " + minutes);
		}
		setBoostChargingTimeRaw(minutes - 10);
	}
	
	void setEqualizingChargingIntervalRaw(int value);
	default void setEqualizingChargingIntervalDays(int days){
		if(days == 0){
			setEqualizingChargingIntervalRaw(0);
		} else {
			if(days <= 5){
				throw new IllegalArgumentException("days cannot be less than or equal to 5! it was: " + days);
			}
			if(days > 300){
				throw new IllegalArgumentException("days cannot be greater than 300! it was: " + days);
			}
			setEqualizingChargingIntervalRaw(days - 5);
		}
	}
	
	void setTemperatureCompensationFactorRaw(int value);
	default void setTemperatureCompensationFactor(int value){
		if(value == 0){
			setTemperatureCompensationFactorRaw(0);
		} else {
			if(value <= 1){
				throw new IllegalArgumentException("value cannot be less than or equal to 1! it was: " + value);
			}
			if(value > 6){
				throw new IllegalArgumentException("value cannot be greater than 6! it was: " + value);
			}
			setTemperatureCompensationFactorRaw(value - 1);
		}
	}
	// TODO add Operating Stage setters
	
	void setLoadWorkingMode(LoadWorkingMode loadWorkingMode);
	
	void setLightControlDelayMinutes(int minutes);
	void setLightControlVoltage(int voltage);
	
	void setLEDLoadCurrentSettingRaw(int value);
	default void setLEDLoadCurrentSettingMilliAmps(int milliAmps){
		if(milliAmps % 10 != 0){
			throw new IllegalArgumentException("milliAmps must a multiple of 10! it was: " + milliAmps);
		}
		setLEDLoadCurrentSettingRaw(milliAmps / 10);
	}
	
	void setSpecialPowerControlE021Raw(int value); // TODO create mutable SpecialPowerControl_E021
	
	// TODO add PowerSensing setters
	
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
	default void setLEDLoadCurrent(int milliAmps){
		if(milliAmps % 10 != 0){
			throw new IllegalArgumentException("milliAmps must a multiple of 10! it was: " + milliAmps);
		}
		setLEDLoadCurrentRaw(milliAmps / 10);
	}
	
	void setSpecialPowerControlE02DRaw(int value);
}
