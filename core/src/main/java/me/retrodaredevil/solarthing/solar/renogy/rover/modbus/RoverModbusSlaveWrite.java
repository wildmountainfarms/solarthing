package me.retrodaredevil.solarthing.solar.renogy.rover.modbus;

import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.modbus.handling.WriteMultipleRegisters;
import me.retrodaredevil.io.modbus.handling.WriteSingleRegister;
import me.retrodaredevil.solarthing.solar.renogy.BatteryType;
import me.retrodaredevil.solarthing.solar.renogy.Voltage;
import me.retrodaredevil.solarthing.solar.renogy.rover.LoadWorkingMode;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverWriteTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.StreetLight;

import static java.util.Objects.requireNonNull;
import static me.retrodaredevil.io.modbus.ModbusMessages.get8BitDataFrom16BitArray;
import static me.retrodaredevil.solarthing.solar.renogy.rover.special.UpperLower16Bit.getCombined;
import static me.retrodaredevil.util.NumberUtil.checkRange;

public class RoverModbusSlaveWrite implements RoverWriteTable {
	private final ModbusSlave modbus;

	public RoverModbusSlaveWrite(ModbusSlave modbus) {
		this.modbus = modbus;
	}

	private void write(int register, int value){
		modbus.sendRequestMessage(new WriteSingleRegister(register, value));
	}

	@Override
	public void factoryReset() {
		modbus.sendRequestMessage(RoverMessageHandler.createRoverFactoryReset());
	}

	@Override
	public void clearHistory() {
		modbus.sendRequestMessage(RoverMessageHandler.createRoverClearHistory());
	}

	@Override
	public void setControllerDeviceAddress(int address) {
		write(0x001A, address);
	}

	@Override
	public void setStreetLightStatus(StreetLight streetLightStatus) {
		requireNonNull(streetLightStatus);
		write(0x010A, streetLightStatus == StreetLight.ON ? 1 : 0);
	}

	@Override
	public void setStreetLightBrightnessPercent(int brightnessPercent) {
		checkRange(0, 100, brightnessPercent);
		write(0xE001, brightnessPercent);
	}
	@Override
	public void setBatteryParameters(
			Voltage systemVoltage,
			BatteryType batteryType,
			int overVoltageThreshold, int chargingVoltageLimit, int equalizingChargingVoltage, int boostChargingVoltage,
			int floatingChargingVoltage, int boostChargingRecoveryVoltage, int overDischargeRecoveryVoltage,
			int underVoltageWarningLevel, int overDischargeVoltage, int dischargingLimitVoltage,
			int endOfChargeSOCValue, int endOfDischargeSOCValue,
			int overDischargeTimeDelaySeconds, int equalizingChargingTimeMinutes, int boostChargingTimeMinutes,
			int equalizingChargingIntervalDays, int temperatureCompensationFactor
	) {
		modbus.sendRequestMessage(new WriteMultipleRegisters(0xE002, get8BitDataFrom16BitArray(
				200, // we need to set this parameter, but it can't be changed and 200 is the default
				systemVoltage.getValueCode() << 8,
				batteryType.getValueCode(),
				overVoltageThreshold, chargingVoltageLimit, equalizingChargingVoltage, boostChargingVoltage,
				floatingChargingVoltage, boostChargingRecoveryVoltage, overDischargeRecoveryVoltage,
				underVoltageWarningLevel, overDischargeVoltage, dischargingLimitVoltage,
				(endOfChargeSOCValue << 8) | endOfDischargeSOCValue,
				overDischargeTimeDelaySeconds, equalizingChargingTimeMinutes, boostChargingTimeMinutes,
				equalizingChargingIntervalDays, temperatureCompensationFactor
		)));
	}

	@Override
	public void setSystemVoltageSetting(Voltage voltage) {
		int value = voltage.getValueCode();
//		write(0xE003, (value << 8) | value); // NOTTODO I don't know if I'm supposed to set the upper or lower 8 bits, so set both for now
		// recognized is on lower bits
		// lower bits will be 0
		write(0xE003, value << 8);
	}

	@Override
	public void setBatteryType(BatteryType batteryType) {
		write(0xE004, batteryType.getValueCode());
	}


	// region 17 set point values
	@Override
	public void setOverVoltageThresholdRaw(int value) {
		checkRange(70, 170, value);
		write(0xE005, value);
//		write(0x0103, value);
	}

	@Override
	public void setChargingVoltageLimitRaw(int value) {
		checkRange(70, 170, value);
		write(0xE006, value);
	}

	@Override
	public void setEqualizingChargingVoltageRaw(int value) {
		checkRange(70, 170, value);
		write(0xE007, value);
	}

	@Override
	public void setBoostChargingVoltageRaw(int value) {
		checkRange(70, 170, value);
		write(0xE008, value);
	}

	@Override
	public void setFloatingChargingVoltageRaw(int value) {
		checkRange(70, 170, value);
		write(0xE009, value);
	}

	@Override
	public void setBoostChargingRecoveryVoltageRaw(int value) {
		checkRange(70, 170, value);
		write(0xE00A, value);
	}

	@Override
	public void setOverDischargeRecoveryVoltageRaw(int value) {
		checkRange(70, 170, value);
		write(0xE00B, value);
	}

	@Override
	public void setUnderVoltageWarningLevelRaw(int value) {
		checkRange(70, 170, value);
		write(0xE00C, value);
	}

	@Override
	public void setOverDischargeVoltageRaw(int value) {
		checkRange(70, 170, value);
		write(0xE00D, value);
	}

	@Override
	public void setDischargingLimitVoltageRaw(int value) {
		checkRange(70, 170, value);
		write(0xE00E, value);
	}

	@Override
	public void setEndOfChargeSOCEndOfDischargeSOC(int endOfChargeSOCValue, int endOfDischargeSOCValue) {
		checkRange(0, 100, endOfChargeSOCValue);
		checkRange(0, 100, endOfDischargeSOCValue);
		write(0xE00F, getCombined(endOfChargeSOCValue, endOfDischargeSOCValue));
	}

	@Override
	public void setOverDischargeTimeDelaySeconds(int seconds) {
		checkRange(0, 120, seconds);
		write(0xE010, seconds);
	}

	@Override
	public void setEqualizingChargingTimeRaw(int value) {
		checkRange(0, 300, value);
		write(0xE011, value);
	}

	@Override
	public void setBoostChargingTimeRaw(int value) {
		checkRange(10, 300, value);
		write(0xE012, value);
	}

	@Override
	public void setEqualizingChargingIntervalRaw(int value) {
		checkRange(0, 255, value);
		write(0xE013, value);
	}

	@Override
	public void setTemperatureCompensationFactorRaw(int value) {
		checkRange(0, 5, value);
		write(0xE014, value);
	}
	// endregion

	@Override
	public void setOperatingDurationHours(OperatingSetting setting, int hours) {
		checkRange(0, 21, hours);
		write(setting.getDurationHoursRegister(), hours);
	}

	@Override
	public void setOperatingPowerPercentage(OperatingSetting setting, int operatingPowerPercentage) {
		checkRange(0, 100, operatingPowerPercentage);
		write(setting.getOperatingPowerPercentageRegister(), operatingPowerPercentage);
	}

	@Override
	public void setLoadWorkingMode(LoadWorkingMode loadWorkingMode) {
		write(0xE01D, loadWorkingMode.getValueCode());
	}

	@Override
	public void setLightControlDelayMinutes(int minutes) {
		checkRange(0, 60, minutes);
		write(0xE01E, minutes);
	}

	@Override
	public void setLightControlVoltage(int voltage) {
		checkRange(1, 40, voltage);
		write(0xE01F, voltage);
	}

	@Override
	public void setLEDLoadCurrentSettingRaw(int value) {
		// TODO figure out allowed range
		checkRange(0, (1 << 16) - 1, value);
		write(0xE020, value);
	}

	@Override
	public void setSpecialPowerControlE021Raw(int value) {
		checkRange(0, (1 << 16) - 1, value);
		write(0xE021, value);
	}

	@Override
	public void setWorkingHoursRaw(Sensing sensing, int value) {
		checkRange(0, 15, value);
		write(sensing.getWorkingHoursRegister(), value);
	}

	@Override
	public void setPowerWithPeopleSensedRaw(Sensing sensing, int value) {
		checkRange(0, 100, value);
		write(sensing.getPowerWithPeopleSensedRegister(), value);
	}

	@Override
	public void setPowerWithNoPeopleSensedRaw(Sensing sensing, int value) {
		checkRange(0, 100, value);
		write(sensing.getPowerWithNoPeopleSensedRegister(), value);
	}

	@Override
	public void setSensingTimeDelayRaw(int value) {
		checkRange(0, 250, value);
		write(0xE02B, value);
	}

	@Override
	public void setLEDLoadCurrentRaw(int value) {
		// TODO figure out allowed range
		checkRange(0, 1 << 16, value);
		write(0xE02C, value);
	}

	@Override
	public void setSpecialPowerControlE02DRaw(int value) {
		checkRange(0, 1 << 16, value);
		write(0xE02D, value);
	}
}
