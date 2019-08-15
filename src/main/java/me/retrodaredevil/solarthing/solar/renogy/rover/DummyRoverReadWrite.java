package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.renogy.BatteryType;
import me.retrodaredevil.solarthing.solar.renogy.Voltage;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class DummyRoverReadWrite implements RoverReadTable, RoverWriteTable {
	public interface OnChange {
		void onChange(String fieldName, String previousValue, String newValue);
	}
	private final RoverReadTable roverReadTable;
	private final OnChange onChange;
	
	private int controllerDeviceAddress;
	private int streetLightValue;
	private Voltage systemVoltageSetting;
	private BatteryType batteryType;
	
	private int overVoltageThresholdRaw;
	private int chargingVoltageLimitRaw;
	private int equalizingChargingVoltageRaw;
	private int boostChargingVoltageRaw;
	private int floatingChargingVoltageRaw;
	private int boostChargingRecoveryVoltageRaw;
	private int overDischargeRecoveryVoltageRaw;
	private int underVoltageWarningLevelRaw;
	private int overDischargeVoltageRaw;
	private int dischargingLimitVoltageRaw;
	
	private int overDischargeTimeDelaySeconds;
	private int equalizingChargingTimeRaw;
	private int boostChargingTimeRaw;
	private int equalizingChargingIntervalRaw;
	private int temperatureCompensationFactorRaw;
	
	private int endOfChargeSOC;
	private int endOfDischargeSOC;
	
	private final Map<OperatingSetting, Integer> operatingDurationHoursMap = new HashMap<>();
	private final Map<OperatingSetting, Integer> operatingPowerPercentageMap = new HashMap<>();
	
	private LoadWorkingMode loadWorkingMode;
	private int lightControlDelayMinutes;
	private int lightControlVoltage;
	
	private int ledLoadCurrentSettingRaw;
	private int specialPowerControlE021Raw;
	
	private final Map<Sensing, Integer> workingHoursRawMap = new HashMap<>();
	private final Map<Sensing, Integer> powerWithPeopleSensedRawMap = new HashMap<>();
	private final Map<Sensing, Integer> powerWithNoPeopleSensedRawMap = new HashMap<>();
	
	private int sensingTimeDelayRaw;
	private int ledLoadCurrentRaw;
	private int specialPowerControlE02DRaw;
	
	public DummyRoverReadWrite(RoverReadTable roverReadTable, OnChange onChange) {
		this.roverReadTable = roverReadTable;
		this.onChange = onChange;
		
		controllerDeviceAddress = roverReadTable.getControllerDeviceAddress();
		streetLightValue = roverReadTable.getStreetLightValue();
		systemVoltageSetting = roverReadTable.getSystemVoltageSetting();
		batteryType = roverReadTable.getBatteryType();
		
		overVoltageThresholdRaw = roverReadTable.getOverVoltageThresholdRaw();
		chargingVoltageLimitRaw = roverReadTable.getChargingVoltageLimitRaw();
		equalizingChargingVoltageRaw = roverReadTable.getEqualizingChargingVoltageRaw();
		boostChargingVoltageRaw = roverReadTable.getBoostChargingVoltageRaw();
		floatingChargingVoltageRaw = roverReadTable.getFloatingChargingVoltageRaw();
		boostChargingRecoveryVoltageRaw = roverReadTable.getBoostChargingRecoveryVoltageRaw();
		overDischargeRecoveryVoltageRaw = roverReadTable.getOverDischargeRecoveryVoltageRaw();
		underVoltageWarningLevelRaw = roverReadTable.getUnderVoltageWarningLevelRaw();
		overDischargeVoltageRaw = roverReadTable.getOverDischargeVoltageRaw();
		dischargingLimitVoltageRaw = roverReadTable.getDischargingLimitVoltageRaw();
		
		overDischargeTimeDelaySeconds = roverReadTable.getOverDischargeTimeDelaySeconds();
		equalizingChargingTimeRaw = roverReadTable.getEqualizingChargingTimeRaw();
		boostChargingTimeRaw = roverReadTable.getBoostChargingTimeRaw();
		equalizingChargingIntervalRaw = roverReadTable.getEqualizingChargingIntervalRaw();
		temperatureCompensationFactorRaw = roverReadTable.getTemperatureCompensationFactorRaw();
		
		endOfChargeSOC = roverReadTable.getEndOfChargeSOC();
		endOfDischargeSOC = roverReadTable.getEndOfDischargeSOC();
		
		for(OperatingSetting setting : OperatingSetting.values()) {
			operatingDurationHoursMap.put(setting, roverReadTable.getOperatingDurationHours(setting));
			operatingPowerPercentageMap.put(setting, roverReadTable.getOperatingPowerPercentage(setting));
		}
		
		loadWorkingMode = roverReadTable.getLoadWorkingMode();
		lightControlDelayMinutes = roverReadTable.getLightControlDelayMinutes();
		lightControlVoltage = roverReadTable.getLightControlVoltage();
		
		ledLoadCurrentSettingRaw = roverReadTable.getLEDLoadCurrentSettingRaw();
		specialPowerControlE021Raw = roverReadTable.getSpecialPowerControlE021Raw();
		
		for(Sensing sensing : Sensing.values()){
			workingHoursRawMap.put(sensing, roverReadTable.getWorkingHoursRaw(sensing));
			powerWithPeopleSensedRawMap.put(sensing, roverReadTable.getPowerWithNoPeopleSensedRaw(sensing));
			powerWithNoPeopleSensedRawMap.put(sensing, roverReadTable.getPowerWithNoPeopleSensedRaw(sensing));
		}
		sensingTimeDelayRaw = roverReadTable.getSensingTimeDelayRaw();
		ledLoadCurrentRaw = roverReadTable.getLEDLoadCurrentRaw();
		specialPowerControlE02DRaw = roverReadTable.getSpecialPowerControlE02DRaw();
	}
	
	
	@Override
	public RoverIdentifier getIdentifier() {
		return new RoverIdentifier(getProductSerialNumber());
	}
	
	@Override public int getMaxVoltageValue() { return roverReadTable.getMaxVoltageValue(); }
	@Override public int getRatedChargingCurrentValue() { return roverReadTable.getRatedChargingCurrentValue(); }
	@Override public int getRatedDischargingCurrentValue() { return roverReadTable.getRatedDischargingCurrentValue(); }
	@Override public int getProductTypeValue() { return roverReadTable.getProductTypeValue(); }
	@Override public byte[] getProductModelValue() { return roverReadTable.getProductModelValue(); }
	@Override public int getSoftwareVersionValue() { return roverReadTable.getSoftwareVersionValue(); }
	@Override public int getHardwareVersionValue() { return roverReadTable.getHardwareVersionValue(); }
	@Override public int getProductSerialNumber() { return roverReadTable.getProductSerialNumber(); }
	@Override
	public int getControllerDeviceAddress() {
		return controllerDeviceAddress;
	}
	@Override
	public void setControllerDeviceAddress(int address) {
		String old = "" + this.controllerDeviceAddress;
		this.controllerDeviceAddress = address;
		onChange.onChange("controllerDeviceAddress", old, "" + address);
	}
	
	@Override public int getBatteryCapacitySOC() { return roverReadTable.getBatteryCapacitySOC(); }
	@Override public float getBatteryVoltage() { return roverReadTable.getBatteryVoltage(); }
	@Override public Float getChargingCurrent() { return roverReadTable.getChargingCurrent(); }
	@Override public int getControllerTemperatureRaw() { return roverReadTable.getControllerTemperatureRaw(); }
	@Override public int getBatteryTemperatureRaw() { return roverReadTable.getBatteryTemperatureRaw(); }
	@Override public float getLoadVoltage() { return roverReadTable.getLoadVoltage(); }
	@Override public float getLoadCurrent() { return roverReadTable.getLoadCurrent(); }
	@Override public int getLoadPower() { return roverReadTable.getLoadPower(); }
	@Override public Float getInputVoltage() { return roverReadTable.getInputVoltage(); }
	@Override public Float getPVCurrent() { return roverReadTable.getPVCurrent(); }
	@Override public Integer getChargingPower() { return roverReadTable.getChargingPower(); }
	@Override public float getDailyMinBatteryVoltage() { return roverReadTable.getDailyMinBatteryVoltage(); }
	@Override public float getDailyMaxBatteryVoltage() { return roverReadTable.getDailyMaxBatteryVoltage(); }
	@Override public float getDailyMaxChargingCurrent() { return roverReadTable.getDailyMaxChargingCurrent(); }
	@Override public float getDailyMaxDischargingCurrent() { return roverReadTable.getDailyMaxDischargingCurrent(); }
	@Override public int getDailyMaxChargingPower() { return roverReadTable.getDailyMaxChargingPower(); }
	@Override public int getDailyMaxDischargingPower() { return roverReadTable.getDailyMaxDischargingPower(); }
	@Override public int getDailyAH() { return roverReadTable.getDailyAH(); }
	@Override public int getDailyAHDischarging() { return roverReadTable.getDailyAHDischarging(); }
	@Override public float getDailyKWH() { return roverReadTable.getDailyKWH(); }
	@Override public float getDailyKWHConsumption() { return roverReadTable.getDailyKWHConsumption(); }
	@Override public int getOperatingDaysCount() { return roverReadTable.getOperatingDaysCount(); }
	@Override public int getBatteryOverDischargesCount() { return roverReadTable.getBatteryOverDischargesCount(); }
	@Override public int getBatteryFullChargesCount() { return roverReadTable.getBatteryFullChargesCount(); }
	@Override public int getChargingAmpHoursOfBatteryCount() { return roverReadTable.getChargingAmpHoursOfBatteryCount(); }
	@Override public int getDischargingAmpHoursOfBatteryCount() { return roverReadTable.getDischargingAmpHoursOfBatteryCount(); }
	@Override public float getCumulativeKWH() { return roverReadTable.getCumulativeKWH(); }
	@Override public float getCumulativeKWHConsumption() { return roverReadTable.getCumulativeKWHConsumption(); }
	
	@Override
	public int getStreetLightValue() {
		return streetLightValue;
	}
	@Override
	public void setStreetLightStatus(StreetLight streetLightStatus) {
		StreetLight oldValue = Modes.getActiveMode(StreetLight.class, streetLightValue);
		switch(requireNonNull(streetLightStatus)){
			case OFF:
				streetLightValue &= StreetLight.IGNORED_BITS;
				break;
			case ON:
				streetLightValue |= (~StreetLight.IGNORED_BITS & 0xFF);
				break;
		}
		onChange.onChange("streetLightStatus", oldValue.getModeName(), streetLightStatus.getModeName());
	}
	
	@Override
	public void setStreetLightBrightnessPercent(int brightnessPercent) {
		final int oldValue = streetLightValue & StreetLight.IGNORED_BITS;
		int value = streetLightValue;
		value &= ~StreetLight.IGNORED_BITS;
		value |= brightnessPercent;
		streetLightValue = value;
		onChange.onChange("streetLightBrightnessPercent", "" + oldValue, "" + brightnessPercent);
	}
	
	@Override public int getChargingStateValue() { return roverReadTable.getChargingStateValue(); }
	@Override public int getErrorMode() { return roverReadTable.getErrorMode(); }
	@Override public int getNominalBatteryCapacity() { return 0; }
	@Override
	public int getSystemVoltageSettingValue() {
		return systemVoltageSetting.getValueCode();
	}
	@Override
	public void setSystemVoltageSetting(Voltage voltage) {
		Voltage oldValue = systemVoltageSetting;
		systemVoltageSetting = voltage;
		onChange.onChange("systemVoltageSetting", oldValue.getModeName(), voltage.getModeName());
	}
	@Override public int getRecognizedVoltageValue() { return roverReadTable.getRecognizedVoltageValue(); }
	@Override
	public int getBatteryTypeValue() {
		return batteryType.getValueCode();
	}
	@Override
	public void setBatteryType(BatteryType batteryType) {
		BatteryType oldType = this.batteryType;
		this.batteryType = batteryType;
		onChange.onChange("batteryType", oldType.getModeName(), batteryType.getModeName());
	}
	
	// region Raw Voltage Configurations
	@Override
	public int getOverVoltageThresholdRaw() {
		return overVoltageThresholdRaw;
	}
	@Override
	public void setOverVoltageThresholdRaw(int value) {
		int old = overVoltageThresholdRaw;
		overVoltageThresholdRaw = value;
		onChange.onChange("overVoltageThresholdRaw", "" + old, "" + value);
	}
	
	@Override
	public int getChargingVoltageLimitRaw() {
		return chargingVoltageLimitRaw;
	}
	@Override
	public void setChargingVoltageLimitRaw(int value) {
		int old = chargingVoltageLimitRaw;
		chargingVoltageLimitRaw = value;
		onChange.onChange("chargingVoltageLimitRaw", "" + old, "" + value);
	}
	
	@Override
	public int getEqualizingChargingVoltageRaw() {
		return equalizingChargingVoltageRaw;
	}
	@Override
	public void setEqualizingChargingVoltageRaw(int value) {
		int old = equalizingChargingIntervalRaw;
		equalizingChargingVoltageRaw = value;
		onChange.onChange("equalizingChargingVoltageRaw", "" + old, "" + value);
	}
	
	@Override
	public int getBoostChargingVoltageRaw() {
		return boostChargingVoltageRaw;
	}
	@Override
	public void setBoostChargingVoltageRaw(int value) {
		int old = boostChargingVoltageRaw;
		boostChargingVoltageRaw = value;
		onChange.onChange("boostChargingVoltageRaw", "" + old, "" + value);
	}
	
	@Override
	public int getFloatingChargingVoltageRaw() {
		return floatingChargingVoltageRaw;
	}
	@Override
	public void setFloatingChargingVoltageRaw(int value) {
		int old = floatingChargingVoltageRaw;
		floatingChargingVoltageRaw = value;
		onChange.onChange("floatingChargingVoltageRaw", "" + old, "" + value);
	}
	
	@Override
	public int getBoostChargingRecoveryVoltageRaw() {
		return boostChargingRecoveryVoltageRaw;
	}
	@Override
	public void setBoostChargingRecoveryVoltageRaw(int value) {
		int old = boostChargingRecoveryVoltageRaw;
		boostChargingRecoveryVoltageRaw = value;
		onChange.onChange("boostChargingRecoveryVoltageRaw", "" + old, "" + value);
	}
	
	@Override
	public int getOverDischargeRecoveryVoltageRaw() {
		return overDischargeRecoveryVoltageRaw;
	}
	@Override
	public void setOverDischargeRecoveryVoltageRaw(int value) {
		int old = overDischargeRecoveryVoltageRaw;
		overDischargeRecoveryVoltageRaw = value;
		onChange.onChange("overDischargeRecoveryVoltageRaw", "" + old, "" + value);
	}
	
	@Override
	public int getUnderVoltageWarningLevelRaw() {
		return underVoltageWarningLevelRaw;
	}
	@Override
	public void setUnderVoltageWarningLevelRaw(int value) {
		int old = underVoltageWarningLevelRaw;
		underVoltageWarningLevelRaw = value;
		onChange.onChange("underVoltageWarningLevelRaw", "" + old, "" + value);
	}
	
	@Override
	public int getOverDischargeVoltageRaw() {
		return overDischargeVoltageRaw;
	}
	@Override
	public void setOverDischargeVoltageRaw(int value) {
		int old = overDischargeVoltageRaw;
		overDischargeVoltageRaw = value;
		onChange.onChange("overDischargeVoltageRaw", "" + old, "" + value);
	}
	
	@Override
	public int getDischargingLimitVoltageRaw() {
		return dischargingLimitVoltageRaw;
	}
	@Override
	public void setDischargingLimitVoltageRaw(int value) {
		int old = dischargingLimitVoltageRaw;
		dischargingLimitVoltageRaw = value;
		onChange.onChange("dischargingLimitVoltageRaw", "" + old, "" + value);
	}
	// endregion
	
	@Override
	public int getEndOfChargeSOC() {
		return endOfChargeSOC;
	}
	
	@Override
	public int getEndOfDischargeSOC() {
		return endOfDischargeSOC;
	}
	
	@Override
	public void setEndOfChargeSOCEndOfDischargeSOC(int endOfChargeSOCValue, int endOfDischargeSOCValue) {
		int oldCharge = endOfChargeSOC;
		int oldDischarge = endOfDischargeSOC;
		this.endOfChargeSOC = endOfChargeSOCValue;
		this.endOfDischargeSOC = endOfDischargeSOCValue;
		onChange.onChange("endOfChargeSOC", "" + oldCharge, "" + endOfChargeSOCValue);
		onChange.onChange("endOfDischargeSOC", "" + oldDischarge, "" + endOfDischargeSOCValue);
	}
	
	@Override
	public int getOverDischargeTimeDelaySeconds() {
		return overDischargeTimeDelaySeconds;
	}
	@Override
	public void setOverDischargeTimeDelaySeconds(int overDischargeTimeDelaySeconds) {
		int old = this.overDischargeTimeDelaySeconds;
		this.overDischargeTimeDelaySeconds = overDischargeTimeDelaySeconds;
		onChange.onChange("overDischargeTimeDelaySeconds", "" + old, "" + overDischargeTimeDelaySeconds);
	}
	
	@Override
	public int getEqualizingChargingTimeRaw() {
		return equalizingChargingTimeRaw;
	}
	@Override
	public void setEqualizingChargingTimeRaw(int equalizingChargingTimeRaw) {
		int old = this.equalizingChargingTimeRaw;
		this.equalizingChargingTimeRaw = equalizingChargingTimeRaw;
		onChange.onChange("equalizingChargingTimeRaw", "" + old, "" + equalizingChargingTimeRaw);
	}
	
	@Override
	public int getBoostChargingTimeRaw() {
		return boostChargingTimeRaw;
	}
	@Override
	public void setBoostChargingTimeRaw(int boostChargingTimeRaw) {
		int old = this.boostChargingTimeRaw;
		this.boostChargingTimeRaw = boostChargingTimeRaw;
		onChange.onChange("boostChargingTimeRaw", "" + old, "" + boostChargingTimeRaw);
	}
	
	@Override
	public int getEqualizingChargingIntervalRaw() {
		return equalizingChargingIntervalRaw;
	}
	@Override
	public void setEqualizingChargingIntervalRaw(int equalizingChargingIntervalRaw) {
		int old = this.equalizingChargingIntervalRaw;
		this.equalizingChargingIntervalRaw = equalizingChargingIntervalRaw;
		onChange.onChange("equalizingChargingIntervalRaw", "" + old, "" + equalizingChargingIntervalRaw);
	}
	
	@Override
	public int getTemperatureCompensationFactorRaw() {
		return temperatureCompensationFactorRaw;
	}
	@Override
	public void setTemperatureCompensationFactorRaw(int temperatureCompensationFactorRaw) {
		int old = this.temperatureCompensationFactorRaw;
		this.temperatureCompensationFactorRaw = temperatureCompensationFactorRaw;
		onChange.onChange("temperatureCompensationFactorRaw", "" + old, "" + temperatureCompensationFactorRaw);
	}
	
	@Override
	public int getOperatingDurationHours(OperatingSetting setting) {
		return operatingDurationHoursMap.get(setting);
	}
	@Override
	public void setOperatingDurationHours(OperatingSetting setting, int hours) {
		int old = getOperatingDurationHours(setting);
		operatingDurationHoursMap.put(setting, hours);
		onChange.onChange(setting + ".operatingDurationHours", "" + old, "" + hours);
	}
	
	@Override
	public int getOperatingPowerPercentage(OperatingSetting setting) {
		return operatingPowerPercentageMap.get(setting);
	}
	@Override
	public void setOperatingPowerPercentage(OperatingSetting setting, int operatingPowerPercentage) {
		int old = operatingPowerPercentageMap.get(setting);
		operatingPowerPercentageMap.put(setting, operatingPowerPercentage);
		onChange.onChange(setting + ".operatingPowerPercentage", "" + old, "" + operatingPowerPercentage);
	}
	
	@Override
	public int getLoadWorkingModeValue() {
		return loadWorkingMode.getValueCode();
	}
	
	@Override
	public void setLoadWorkingMode(LoadWorkingMode loadWorkingMode) {
		LoadWorkingMode old = this.loadWorkingMode;
		this.loadWorkingMode = loadWorkingMode;
		onChange.onChange("loadWorkingMode", "" + old, "" + loadWorkingMode);
	}
	
	@Override
	public int getLightControlDelayMinutes() {
		return lightControlDelayMinutes;
	}
	@Override
	public void setLightControlDelayMinutes(int minutes) {
		int old = lightControlDelayMinutes;
		lightControlDelayMinutes = minutes;
		onChange.onChange("lightControlDelayMinutes", "" + old, "" + minutes);
	}
	
	@Override
	public int getLightControlVoltage() {
		return lightControlVoltage;
	}
	@Override
	public void setLightControlVoltage(int voltage) {
		int old = lightControlVoltage;
		lightControlVoltage = voltage;
		onChange.onChange("lightControlVoltage", "" + old, "" + voltage);
	}
	
	@Override
	public int getLEDLoadCurrentSettingRaw() {
		return ledLoadCurrentSettingRaw;
	}
	@Override
	public void setLEDLoadCurrentSettingRaw(int value) {
		int old = ledLoadCurrentSettingRaw;
		ledLoadCurrentSettingRaw = value;
		onChange.onChange("ledLoadCurrentSettingRaw", "" + old, "" + value);
	}
	
	@Override
	public int getSpecialPowerControlE021Raw() {
		return specialPowerControlE021Raw;
	}
	@Override
	public void setSpecialPowerControlE021Raw(int value) {
		int old = specialPowerControlE021Raw;
		specialPowerControlE021Raw = value;
		onChange.onChange("specialPowerControlE021Raw", "" + old, "" + value);
	}
	
	@Override
	public int getWorkingHoursRaw(Sensing sensing) {
		return workingHoursRawMap.get(sensing);
	}
	@Override
	public void setWorkingHoursRaw(Sensing sensing, int value) {
		int old = getWorkingHoursRaw(sensing);
		workingHoursRawMap.put(sensing, value);
		onChange.onChange("workingHoursRaw", "" + old, "" + value);
	}
	
	@Override
	public int getPowerWithPeopleSensedRaw(Sensing sensing) {
		return powerWithPeopleSensedRawMap.get(sensing);
	}
	@Override
	public void setPowerWithPeopleSensedRaw(Sensing sensing, int value) {
		int old = getPowerWithPeopleSensedRaw(sensing);
		powerWithPeopleSensedRawMap.put(sensing, value);
		onChange.onChange("powerWithPeopleSensedRaw", "" + old, "" + value);
	}
	
	@Override
	public int getPowerWithNoPeopleSensedRaw(Sensing sensing) {
		return powerWithNoPeopleSensedRawMap.get(sensing);
	}
	@Override
	public void setPowerWithNoPeopleSensedRaw(Sensing sensing, int value) {
		int old = getPowerWithNoPeopleSensedRaw(sensing);
		powerWithNoPeopleSensedRawMap.put(sensing, value);
		onChange.onChange("powerWithNoPeopleSensedRaw", "" + old, "" + value);
	}
	
	@Override
	public int getSensingTimeDelayRaw() {
		return sensingTimeDelayRaw;
	}
	@Override
	public void setSensingTimeDelayRaw(int sensingTimeDelayRaw) {
		int old = this.sensingTimeDelayRaw;
		this.sensingTimeDelayRaw = sensingTimeDelayRaw;
		onChange.onChange("sensingTimeDelayRaw", "" + old, "" + sensingTimeDelayRaw);
	}
	
	@Override
	public int getLEDLoadCurrentRaw() {
		return ledLoadCurrentRaw;
	}
	@Override
	public void setLEDLoadCurrentRaw(int value) {
		int old = ledLoadCurrentRaw;
		ledLoadCurrentRaw = value;
		onChange.onChange("ledLoadCurrentRaw", "" + old, "" + value);
	}
	
	@Override
	public int getSpecialPowerControlE02DRaw() {
		return specialPowerControlE02DRaw;
	}
	@Override
	public void setSpecialPowerControlE02DRaw(int specialPowerControlE02DRaw) {
		int old = this.specialPowerControlE02DRaw;
		this.specialPowerControlE02DRaw = specialPowerControlE02DRaw;
		onChange.onChange("specialPowerControlE02D", "" + old, "" + specialPowerControlE02DRaw);
	}
}
