package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.modbus.ModbusWrite;
import me.retrodaredevil.solarthing.solar.renogy.BatteryType;
import me.retrodaredevil.solarthing.solar.renogy.Voltage;

import static java.util.Objects.requireNonNull;
import static me.retrodaredevil.util.NumberUtil.checkRange;

public class RoverModbusWrite implements RoverWriteTable {
	private final ModbusWrite modbus;
	
	public RoverModbusWrite(ModbusWrite modbus) {
		this.modbus = modbus;
	}
	
	@Override
	public void setControllerDeviceAddress(int address) {
		modbus.writeRegister(0x001A, address);
	}
	
	@Override
	public void setStreetLightStatus(StreetLight streetLightStatus) {
		requireNonNull(streetLightStatus);
		modbus.writeRegister(0x010A, streetLightStatus == StreetLight.ON ? 1 : 0);
	}
	
	@Override
	public void setStreetLightBrightnessPercent(int brightnessPercent) {
		checkRange(0, 100, brightnessPercent);
		modbus.writeRegister(0xE001, brightnessPercent);
	}
	
	@Override
	public void setSystemVoltageSetting(Voltage voltage) { // TODO test to see if recognized voltage actually changes because of this
		int value = voltage.getValueCode();
		modbus.writeRegister(0xE003, (value << 16) & value); // TODO I don't know if I'm supposed to set the upper or lower 8 bits, so set both for now
	}
	
	@Override
	public void setBatteryType(BatteryType batteryType) {
		modbus.writeRegister(0xE004, batteryType.getValueCode());
	}
	
	@Override
	public void setOverVoltageThresholdRaw(int value) {
		checkRange(70, 170, value);
		modbus.writeRegister(0xE005, value);
	}
	
	@Override
	public void setChargingVoltageLimitRaw(int value) {
		checkRange(70, 170, value);
		modbus.writeRegister(0xE006, value);
	}
	
	@Override
	public void setEqualizingChargingVoltageRaw(int value) {
		checkRange(70, 170, value);
		modbus.writeRegister(0xE007, value);
	}
	
	@Override
	public void setBoostChargingVoltageRaw(int value) {
		checkRange(70, 170, value);
		modbus.writeRegister(0xE008, value);
	}
	
	@Override
	public void setFloatingChargingVoltageRaw(int value) {
		checkRange(70, 170, value);
		modbus.writeRegister(0xE009, value);
	}
	
	@Override
	public void setBoostChargingRecoveryVoltageRaw(int value) {
		checkRange(70, 170, value);
		modbus.writeRegister(0xE00A, value);
	}
	
	@Override
	public void setOverDischargeRecoveryVoltageRaw(int value) {
		checkRange(70, 170, value);
		modbus.writeRegister(0xE00B, value);
	}
	
	@Override
	public void setUnderVoltageWarningLevelRaw(int value) {
		checkRange(70, 170, value);
		modbus.writeRegister(0xE00C, value);
	}
	
	@Override
	public void setOverDischargeVoltageRaw(int value) {
		checkRange(70, 170, value);
		modbus.writeRegister(0xE00D, value);
	}
	
	@Override
	public void setDischargingLimitVoltageRaw(int value) {
		checkRange(70, 170, value);
		modbus.writeRegister(0xE00E, value);
	}
	
	@Override
	public void setEndOfChargeSOCEndOfDischargeSOC(int endOfChargeSOCValue, int endOfDischargeSOCValue) {
		// TODO figure out allowed range of each
		modbus.writeRegister(0xE00F, (endOfChargeSOCValue << 16) & endOfDischargeSOCValue);
	}
	
	@Override
	public void setOverDischargeTimeDelaySeconds(int seconds) {
		checkRange(0, 120, seconds);
		modbus.writeRegister(0xE010, seconds);
	}
	
	@Override
	public void setEqualizingChargingTimeRaw(int value) {
		checkRange(0, 300, value);
		modbus.writeRegister(0xE011, value);
	}
	
	@Override
	public void setBoostChargingTimeRaw(int value) {
		checkRange(10, 300, value);
		modbus.writeRegister(0xE012, value);
	}
	
	@Override
	public void setEqualizingChargingIntervalRaw(int value) {
		checkRange(0, 255, value);
		modbus.writeRegister(0xE013, value);
	}
	
	@Override
	public void setTemperatureCompensationFactorRaw(int value) {
		checkRange(0, 5, value);
		modbus.writeRegister(0xE014, value);
	}
	
	@Override
	public void setDurationHours(OperatingSetting setting, int hours) {
		// TODO get range
		modbus.writeRegister(setting.getDurationHoursRegister(), hours);
	}
	
	@Override
	public void setOperatingPowerPercentage(OperatingSetting setting, int operatingPowerPercentage) {
		checkRange(0, 100, operatingPowerPercentage);
		modbus.writeRegister(setting.getOperatingPowerPercentageRegister(), operatingPowerPercentage);
	}
	
	@Override
	public void setLoadWorkingMode(LoadWorkingMode loadWorkingMode) {
		modbus.writeRegister(0xE01D, loadWorkingMode.getValueCode());
	}
	
	@Override
	public void setLightControlDelayMinutes(int minutes) {
		checkRange(0, 60, minutes);
		modbus.writeRegister(0xE01E, minutes);
	}
	
	@Override
	public void setLightControlVoltage(int voltage) {
		checkRange(1, 40, voltage);
		modbus.writeRegister(0xE01F, voltage);
	}
	
	@Override
	public void setLEDLoadCurrentSettingRaw(int value) {
		// TODO figure out allowed range
		checkRange(0, 1 << 16, value);
		modbus.writeRegister(0xE020, value);
	}
	
	@Override
	public void setSpecialPowerControlE021Raw(int value) {
		checkRange(0, 1 << 16, value);
		modbus.writeRegister(0xE021, value);
	}
	
	@Override
	public void setSensingTimeDelayRaw(int value) {
		checkRange(0, 250, value);
		modbus.writeRegister(0xE02B, value);
	}
	
	@Override
	public void setLEDLoadCurrentRaw(int value) {
		// TODO figure out allowed range
		checkRange(0, 1 << 16, value);
		modbus.writeRegister(0xE02C, value);
	}
	
	@Override
	public void setSpecialPowerControlE02DRaw(int value) {
		checkRange(0, 1 << 16, value);
		modbus.writeRegister(0xE02D, value);
	}
}
