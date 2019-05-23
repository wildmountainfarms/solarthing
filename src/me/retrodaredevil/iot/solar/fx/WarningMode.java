package me.retrodaredevil.iot.solar.fx;

import me.retrodaredevil.iot.packets.BitmaskMode;

/**
 * The warning modes for the fx
 */
public enum WarningMode implements BitmaskMode { // multiple can be active (or 0)
	AC_INPUT_FREQ_HIGH(1, "AC Input Freq High"),
	AC_INPUT_FREQ_LOW(2, "AC Input Freq Low"),
	INPUT_VAC_HIGH(4, "Input VAC High"),
	INPUT_VAC_LOW(8, "Input VAC Low"),
	BUY_AMPS_GT_INPUT_SIZE(16, "Buy Amps > Input size"),
	TEMP_SENSOR_FAILED(32, "Temp Sensor failed"),
	COMM_ERROR(64, "Comm Error"),
	FAN_FAILURE(128, "Fan Failure");

	private int value;
	private String name;
	WarningMode(int value, String name){
		this.value = value;
		this.name = name;
	}

	@Override
	public int getMaskValue() {
		return value;
	}

	@Override
	public String getModeName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}

