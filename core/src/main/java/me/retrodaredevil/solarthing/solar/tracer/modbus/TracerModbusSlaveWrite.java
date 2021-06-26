package me.retrodaredevil.solarthing.solar.tracer.modbus;

import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.modbus.handling.WriteMultipleRegisters;
import me.retrodaredevil.io.modbus.handling.WriteSingleCoil;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.solar.tracer.TracerWriteTable;
import me.retrodaredevil.solarthing.solar.tracer.mode.*;
import me.retrodaredevil.solarthing.solar.util.AbstractModbusWrite;

import static me.retrodaredevil.io.modbus.ModbusMessages.get8BitDataFrom16BitArray;

public class TracerModbusSlaveWrite extends AbstractModbusWrite implements TracerWriteTable {
	public TracerModbusSlaveWrite(ModbusSlave modbus) {
		super(modbus);
	}

	private static int convertDecimalToRaw(float decimal) {
		return Math.round(decimal * 100.0f);
	}
	private static int[] convert48BitLongTo8BitArray(long raw) {
		int[] data16Bit = new int[3];
		data16Bit[0] = (int) (raw & 0xFFFFL);
		data16Bit[1] = (int) ((raw >> 16) & 0xFFFFL);
		data16Bit[2] = (int) ((raw >> 32) & 0xFFFFL);
		return get8BitDataFrom16BitArray(data16Bit);
	}

	@Override
	public void setBatteryType(TracerBatteryType batteryType) {
		write(0x9000, batteryType.getValueCode());
	}

	@Override
	public void setBatteryCapacityAmpHours(int batteryCapacityAmpHours) {
		write(0x9001, batteryCapacityAmpHours);
	}

	@Override
	public void setTemperatureCompensationCoefficient(int temperatureCompensationCoefficient) {
		write(0x9002, 100 * temperatureCompensationCoefficient);
	}

	@Override
	public void setHighVoltageDisconnect(float highVoltageDisconnect) {
		write(0x9003, convertDecimalToRaw(highVoltageDisconnect));
	}

	@Override
	public void setChargingLimitVoltage(float chargingLimitVoltage) {
		write(0x9004, convertDecimalToRaw(chargingLimitVoltage));
	}

	@Override
	public void setOverVoltageReconnect(float overVoltageReconnect) {
		write(0x9005, convertDecimalToRaw(overVoltageReconnect));
	}

	@Override
	public void setEqualizationVoltage(float equalizationVoltage) {
		write(0x9006, convertDecimalToRaw(equalizationVoltage));
	}

	@Override
	public void setBoostVoltage(float boostVoltage) {
		write(0x9007, convertDecimalToRaw(boostVoltage));
	}

	@Override
	public void setFloatVoltage(float floatVoltage) {
		write(0x9008, convertDecimalToRaw(floatVoltage));
	}

	@Override
	public void setBoostReconnectVoltage(float boostReconnectVoltage) {
		write(0x9009, convertDecimalToRaw(boostReconnectVoltage));
	}

	@Override
	public void setLowVoltageReconnect(float lowVoltageReconnect) {
		write(0x900A, convertDecimalToRaw(lowVoltageReconnect));
	}

	@Override
	public void setUnderVoltageRecover(float underVoltageRecover) {
		write(0x900B, convertDecimalToRaw(underVoltageRecover));
	}

	@Override
	public void setUnderVoltageWarning(float underVoltageWarning) {
		write(0x900C, convertDecimalToRaw(underVoltageWarning));
	}

	@Override
	public void setLowVoltageDisconnect(float lowVoltageDisconnect) {
		write(0x900D, convertDecimalToRaw(lowVoltageDisconnect));
	}

	@Override
	public void setDischargingLimitVoltage(float dischargingLimitVoltage) {
		write(0x900E, convertDecimalToRaw(dischargingLimitVoltage));
	}

	@Override
	public void setSecondMinuteHourDayMonthYearRaw(long raw) {
		modbus.sendRequestMessage(new WriteMultipleRegisters(0x9013, convert48BitLongTo8BitArray(raw)));
	}

	@Override
	public void setEqualizationChargingCycleDays(int equalizationChargingCycleDays) {
		write(0x9016, equalizationChargingCycleDays);
	}

	@Override
	public void setBatteryTemperatureWarningUpperLimit(float batteryTemperatureWarningUpperLimit) {
		write(0x9017, convertDecimalToRaw(batteryTemperatureWarningUpperLimit));
	}

	@Override
	public void setBatteryTemperatureWarningLowerLimit(float batteryTemperatureWarningLowerLimit) {
		write(0x9018, convertDecimalToRaw(batteryTemperatureWarningLowerLimit));
	}

	@Override
	public void setInsideControllerTemperatureWarningUpperLimit(float insideControllerTemperatureWarningUpperLimit) {
		write(0x9019, convertDecimalToRaw(insideControllerTemperatureWarningUpperLimit));
	}

	@Override
	public void setInsideControllerTemperatureWarningUpperLimitRecover(float insideControllerTemperatureWarningUpperLimitRecover) {
		write(0x901A, convertDecimalToRaw(insideControllerTemperatureWarningUpperLimitRecover));
	}

	@Override
	public void setPowerComponentTemperatureUpperLimit(float powerComponentTemperatureUpperLimit) {
		write(0x901B, convertDecimalToRaw(powerComponentTemperatureUpperLimit));
	}

	@Override
	public void setPowerComponentTemperatureUpperLimitRecover(float powerComponentTemperatureUpperLimitRecover) {
		write(0x901C, convertDecimalToRaw(powerComponentTemperatureUpperLimitRecover));
	}

	@Override
	public void setLineImpedance(float lineImpedance) {
		write(0x901D, convertDecimalToRaw(lineImpedance));
	}

	@Override
	public void setNightPVVoltageThreshold(float nightPVVoltageThreshold) {
		write(0x901E, convertDecimalToRaw(nightPVVoltageThreshold));
	}

	@Override
	public void setLightSignalStartupDelayTime(int lightSignalStartupDelayTime) {
		write(0x901F, lightSignalStartupDelayTime);
	}

	@Override
	public void setDayPVVoltageThreshold(float dayPVVoltageThreshold) {
		write(0x9020, convertDecimalToRaw(dayPVVoltageThreshold));
	}

	@Override
	public void setLightSignalTurnOffDelayTime(int lightSignalTurnOffDelayTime) {
		write(0x9021, lightSignalTurnOffDelayTime);
	}

	@Override
	public void setLoadControlMode(LoadControlMode loadControlMode) {
		write(0x903D, loadControlMode.getValueCode());
	}

	@Override
	public void setWorkingTimeLength1Raw(int workingTimeLength1Raw) {
		write(0x903E, workingTimeLength1Raw);
	}

	@Override
	public void setWorkingTimeLength2Raw(int workingTimeLength2Raw) {
		write(0x903F, workingTimeLength2Raw);
	}

	@Override
	public void setTurnOnTiming1Raw(long turnOnTiming1Raw) {
		modbus.sendRequestMessage(new WriteMultipleRegisters(0x9042, convert48BitLongTo8BitArray(turnOnTiming1Raw)));
	}

	@Override
	public void setTurnOffTiming1Raw(long turnOffTiming1Raw) {
		modbus.sendRequestMessage(new WriteMultipleRegisters(0x9045, convert48BitLongTo8BitArray(turnOffTiming1Raw)));
	}

	@Override
	public void setTurnOnTiming2Raw(long turnOnTiming2Raw) {
		modbus.sendRequestMessage(new WriteMultipleRegisters(0x9048, convert48BitLongTo8BitArray(turnOnTiming2Raw)));
	}

	@Override
	public void setTurnOffTiming2Raw(long turnOffTiming2Raw) {
		modbus.sendRequestMessage(new WriteMultipleRegisters(0x904B, convert48BitLongTo8BitArray(turnOffTiming2Raw)));
	}

	@Override
	public void setLengthOfNightRaw(int lengthOfNightRaw) {
		write(0x9065, lengthOfNightRaw);
	}

	@Override
	public void setBatteryDetection(@NotNull BatteryDetection batteryDetection) {
		write(0x9067, batteryDetection.getValueCode());
	}

	@Override
	public void setLoadTimingControlSelection(@NotNull LoadTimingControlSelection loadTimingControlSelection) {
		write(0x9069, loadTimingControlSelection.getValueCode());
	}

	@Override
	public void setLoadOnByDefaultInManualMode(boolean isLoadOnByDefaultInManualMode) {
		int value = isLoadOnByDefaultInManualMode ? 1 : 0;
		write(0x906A, value);
	}

	@Override
	public void setEqualizeDurationMinutes(int equalizeDurationMinutes) {
		write(0x906B, equalizeDurationMinutes);
	}

	@Override
	public void setBoostDurationMinutes(int boostDurationMinutes) {
		write(0x906C, boostDurationMinutes);
	}

	@Override
	public void setDischargingPercentage(int dischargingPercentage) {
		write(0x906D, dischargingPercentage);
	}

	@Override
	public void setChargingPercentage(int chargingPercentage) {
		write(0x906E, chargingPercentage);
	}

	@Override
	public void setBatteryManagementMode(@NotNull BatteryManagementMode batteryManagementMode) {
		write(0x9070, batteryManagementMode.getValueCode());
	}

	@Override
	public void setManualLoadControlOn(boolean isManualLoadControlOn) {
		modbus.sendRequestMessage(new WriteSingleCoil(2, isManualLoadControlOn));
	}

	@Override
	public void setLoadTestModeEnabled(boolean isLoadTestModeEnabled) {
		modbus.sendRequestMessage(new WriteSingleCoil(5, isLoadTestModeEnabled));
	}

	@Override
	public void setLoadForcedOn(boolean isLoadForcedOn) {
		modbus.sendRequestMessage(new WriteSingleCoil(6, isLoadForcedOn));
	}
}
