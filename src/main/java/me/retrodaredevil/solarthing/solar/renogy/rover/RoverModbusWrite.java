package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.modbus.ModbusWrite;
import me.retrodaredevil.solarthing.solar.renogy.BatteryType;
import me.retrodaredevil.solarthing.solar.renogy.Voltage;

import static java.util.Objects.requireNonNull;

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
		modbus.writeRegister(0xE005, value);
	}
	
	@Override
	public void setChargingVoltageLimitRaw(int value) {
		modbus.writeRegister(0xE006, value);
	}
	
	@Override
	public void setEqualizingChargingVoltageRaw(int value) {
		modbus.writeRegister(0xE007, value);
	}
	
	@Override
	public void setBoostChargingVoltageRaw(int value) {
		modbus.writeRegister(0xE008, value);
	}
	
	@Override
	public void setFloatingChargingVoltageRaw(int value) {
		modbus.writeRegister(0xE009, value);
	}
	
	@Override
	public void setBoostChargingRecoveryVoltageRaw(int value) {
		modbus.writeRegister(0xE00A, value);
	}
	
	@Override
	public void setOverDischargeRecoveryVoltageRaw(int value) {
		modbus.writeRegister(0xE00B, value);
	}
	
	@Override
	public void setUnderVoltageWarningLevelRaw(int value) {
		modbus.writeRegister(0xE00C, value);
	}
	
	@Override
	public void setOverDischargeVoltageRaw(int value) {
		modbus.writeRegister(0xE00D, value);
	}
	
	@Override
	public void setDischargingLimitVoltageRaw(int value) {
		modbus.writeRegister(0xE00E, value);
	}
	
	@Override
	public void setEndOfChargeSOCEndOfDischargeSOC(int endOfChargeSOCValue, int endOfDischargeSOCValue) {
		modbus.writeRegister(0xE00F, (endOfChargeSOCValue << 16) & endOfDischargeSOCValue);
	}
	
	@Override
	public void setOverDischargeTimeDelaySeconds(int seconds) {
		modbus.writeRegister(0xE010, seconds);
	}
	
	@Override
	public void setEqualizingChargingTimeRaw(int value) {
		modbus.writeRegister(0xE011, value);
	}
	
	@Override
	public void setBoostChargingTimeRaw(int value) {
		modbus.writeRegister(0xE012, value);
	}
	
	@Override
	public void setEqualizingChargingIntervalRaw(int value) {
		modbus.writeRegister(0xE013, value);
	}
	
	@Override
	public void setTemperatureCompensationFactorRaw(int value) {
		modbus.writeRegister(0xE014, value);
	}
	
	@Override
	public void setLoadWorkingMode(LoadWorkingMode loadWorkingMode) {
		modbus.writeRegister(0xE01D, loadWorkingMode.getValueCode());
	}
	
	@Override
	public void setLightControlDelayMinutes(int minutes) {
		modbus.writeRegister(0xE01E, minutes);
	}
	
	@Override
	public void setLightControlVoltage(int voltage) {
		modbus.writeRegister(0xE01F, voltage);
	}
	
	@Override
	public void setLEDLoadCurrentSettingRaw(int value) {
		modbus.writeRegister(0xE020, value);
	}
	
	@Override
	public void setSpecialPowerControlE021Raw(int value) {
		modbus.writeRegister(0xE021, value);
	}
	
	@Override
	public void setSensingTimeDelayRaw(int value) {
		modbus.writeRegister(0xE02B, value);
	}
	
	@Override
	public void setLEDLoadCurrentRaw(int value) {
		modbus.writeRegister(0xE02C, value);
	}
	
	@Override
	public void setSpecialPowerControlE02DRaw(int value) {
		modbus.writeRegister(0xE02D, value);
	}
}
