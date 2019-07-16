package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;
import me.retrodaredevil.solarthing.solar.common.ChargeController;
import me.retrodaredevil.solarthing.solar.common.DailyData;
import me.retrodaredevil.solarthing.solar.renogy.BatteryType;
import me.retrodaredevil.solarthing.solar.renogy.ProductType;
import me.retrodaredevil.solarthing.solar.renogy.SystemVoltage;

@SuppressWarnings("unused")
public interface RoverReadTable extends ChargeController, DailyData {
	int getMaxVoltage();
	default MaxVoltage getMaxVoltageMode(){ return Modes.getActiveMode(MaxVoltage.class, getMaxVoltage()); }
	
	int getRatedChargingCurrent();
	default RatedCurrent getRatedChargingCurrentMode(){ return Modes.getActiveMode(RatedCurrent.class, getRatedChargingCurrent()); }
	
	int getRatedDischargingCurrent();
	default RatedCurrent getRatedDischargingCurrentMode(){ return Modes.getActiveMode(RatedCurrent.class, getRatedDischargingCurrent()); }
	
	int getProductType();
	default ProductType getProductTypeMode(){ return Modes.getActiveMode(ProductType.class, getProductType()); }
	
	byte[] getProductModel(); // TODO maybe change this signature
	int getSoftwareVersion();
	int getHardwareVersion();
	int getProductSerialNumber();
	
	/**
	 * Should be serialized as "address"
	 * @return A number in range [1..247] representing the controller device address
	 */
	int getControllerDeviceAddress(); // TODO make a common renogy interface for addresses
	
	/**
	 * @return A number in range [0..100] representing the battery percentage
	 */
	int getBatteryCapacitySOC();
	
	// implements BatteryVoltage
	
	@Override
	Float getChargerCurrent();
	@Override
	Integer getChargingPower();
	
	int getControllerTemperature();
	int getBatteryTemperature();
	
	/** AKA street light voltage*/
	float getLoadVoltage();
	float getLoadCurrent();
	int getLoadPower();
	
	/** AKA PV/Solar Panel voltage*/
	@Override
	Float getInputVoltage();
	@Override
	Float getPVCurrent();
	int getChargerPower();
	
	BatteryVoltage getMinBatteryVoltage();
	BatteryVoltage getMaxBatteryVoltage();
	float getMaxChargerCurrent();
	float getMaxDischargingCurrent();
	int getMaxChargingPower();
	int getMaxDischargingPower();
	
	int getChargingAmpHours();
	int getDischargingAmpHours();
	
	// 0x0113
	@Override
	float getDailyKWH();
	float getDailyKWHConsumption();
	
	int getOperatingDaysCount();
	int getBatteryOverDischargesCount();
	int getBatteryFullChargesCount();
	int getChargingAmpHoursOfBatteryCount();
	int getDischargingAmpHoursOfBatteryCount();
	float getCumulativeKWH();
	float getCumulativeKWHConsumption();
	
	int getStreetLightValue();
	default StreetLight getStreetLightStatus(){ return Modes.getActiveMode(StreetLight.class, getStreetLightValue()); }
	default int getStreetLightBrightnessPercent(){ return StreetLight.getBrightnessValue(getStreetLightValue()); }
	
	int getErrorMode();
	
	int getNominalBatteryCapacity();
	
	/** Should be serialized as "systemVoltageSetting" */
	int getSystemVoltageSettingValue();
	/** Should be serialized as "recognizedVoltage" */
	int getRecognizedVoltageValue();
	default SystemVoltage getSystemVoltageSetting(){ return Modes.getActiveMode(SystemVoltage.class, getSystemVoltageSettingValue()); }
	default SystemVoltage getRecognizedVoltage(){ return Modes.getActiveMode(SystemVoltage.class, getRecognizedVoltageValue()); }
	
	// 0xE004
	/** Should be serialized as "batteryType" */
	int getBatteryTypeValue();
	default BatteryType getBatteryType(){ return Modes.getActiveMode(BatteryType.class, getBatteryTypeValue()); }
	
	int getOverVoltageThresholdRaw(); // TODO add non-raw method that returns a float
	int getChargingVoltageLimitRaw();
	int getEqualizingChargingVoltageRaw();
	/** AKA overcharge voltage (for lithium batteries) */
	int getBoostChargingVoltageRaw();
	/** AKA overcharge recovery voltage (for lithium batteries) */
	int getFloatingChargingVoltageRaw();
	int getBoostChargingRecoveryVoltageRaw();
	int getOverDischargeRecoveryVoltageRaw();
	int getUnderVoltageWarningLevelRaw();
	int getOverDischargeVoltageRaw();
	int getDischargingLimitVoltageRaw();
	
	// 0xE00F
	int getEndOfChargeSOC(); // TODO figure out units and what this means
	int getEndOfDischargeSOC();
	
	int getOverDischargeTimeDelaySeconds();
	
	int getEqualizingChargingTimeRaw();
	int getEqualizingChargingTimeMinutes();
	
	int getBoostChargingTimeRaw();
	int getBoostChargingTimeMinutes();
	
	int getEqualizingChargingIntervalRaw();
	int getEqualizingChargingIntervalDays();
	
	int getTemperatureCompensationFactorRaw();
	/** Units: mV/C/2V*/
	int getTemperatureCompensationFactor();
	
	//0xE015
	OperatingStage getStage1();
	OperatingStage getStage2();
	OperatingStage getStage3();
	OperatingStage getMorningOn();
	
	/** Should be serialized as "loadWorkingMode" */
	int getLoadWorkingModeValue();
	default LoadWorkingMode getLoadWorkingMode() { return Modes.getActiveMode(LoadWorkingMode.class, getLoadWorkingModeValue()); }
	
	int getLightControlDelayMinutes();
	int getLightControlVoltage();
	
	// 0xE020
	int getLEDLoadCurrentSettingRaw();
	/** Units: mA */
	int getLEDLoadCurrentSetting();
	
	int getSpecialPowerControlE021Raw();
	SpecialPowerControl_E021 getSpecialPowerControlE021();
	
	PowerSensing getPowerSensing1();
	PowerSensing getPowerSensing2();
	PowerSensing getPowerSensing3();
	
	int getSensingTimeDelayRaw();
	int getSensingTimeDelaySeconds();
	
	int getLEDLoadCurrentRaw();
	/** Units: mA */
	int getLEDLoadCurrent();
	
	int getSpecialPowerControlE02DRaw();
	SpecialPowerControl_E02D getSpecialPowerControlE0D1();
	
	
	
	final class OperatingStage {
		private final int durationHours;
		private final int operatingPowerPercentage;
		
		public OperatingStage(int durationHours, int operatingPowerPercentage) {
			this.durationHours = durationHours;
			this.operatingPowerPercentage = operatingPowerPercentage;
		}
		
		public int getDurationHours() {
			return durationHours;
		}
		
		public int getOperatingPowerPercentage() {
			return operatingPowerPercentage;
		}
	}
	final class PowerSensing {
		private final int workingHoursRaw;
		private final int workingHours;
		
		private final int powerWithPeopleSensedRaw;
		private final int powerWithPeopleSensed;
		
		private final int powerWithNoPeopleSensedRaw;
		private final int powerWithNoPeopleSensed;
		
		public PowerSensing(int workingHoursRaw, int workingHours, int powerWithPeopleSensedRaw, int powerWithPeopleSensed, int powerWithNoPeopleSensedRaw, int powerWithNoPeopleSensed) {
			this.workingHoursRaw = workingHoursRaw;
			this.workingHours = workingHours;
			this.powerWithPeopleSensedRaw = powerWithPeopleSensedRaw;
			this.powerWithPeopleSensed = powerWithPeopleSensed;
			this.powerWithNoPeopleSensedRaw = powerWithNoPeopleSensedRaw;
			this.powerWithNoPeopleSensed = powerWithNoPeopleSensed;
		}
		
		public int getWorkingHoursRaw() { return workingHoursRaw; }
		public int getWorkingHours() { return workingHours; }
		public int getPowerWithPeopleSensedRaw() { return powerWithPeopleSensedRaw; }
		public int getPowerWithPeopleSensed() { return powerWithPeopleSensed; }
		public int getPowerWithNoPeopleSensedRaw() { return powerWithNoPeopleSensedRaw; }
		public int getPowerWithNoPeopleSensed() { return powerWithNoPeopleSensed; }
	}
	
}
