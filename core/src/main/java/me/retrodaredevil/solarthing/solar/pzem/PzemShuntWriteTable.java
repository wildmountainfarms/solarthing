package me.retrodaredevil.solarthing.solar.pzem;

public interface PzemShuntWriteTable {
	void setHighVoltageAlarmThresholdRaw(int voltageRaw);
	void setLowVoltageAlarmThresholdRaw(int voltageRaw);
	void setModbusAddress(int modbusAddress);

	/**
	 * Only works on PZEM-017s
	 * @param currentRangeRawValue 0 is	100A, 1 is 50A, 2 is 200A, 3 is 300A
	 */
	void setCurrentRangeRaw(int currentRangeRawValue);
}
