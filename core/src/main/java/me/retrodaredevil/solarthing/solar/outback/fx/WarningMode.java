package me.retrodaredevil.solarthing.solar.outback.fx;

import me.retrodaredevil.solarthing.packets.BitmaskMode;

/**
 * The warning modes for the fx
 */
public enum WarningMode implements BitmaskMode { // multiple can be active (or 0)
	/** AC source  is above 66 Hz* (upper limit) and will be dropped if frequency gets much higher*/
	AC_INPUT_FREQ_HIGH(1, "AC Input Freq High"),
	/** AC source is under 54 Hz** (lower limit) and will be dropped if frequency gets much lower*/
	AC_INPUT_FREQ_LOW(2, "AC Input Freq Low"),
	/** AC source’s voltage is over 140VAC*** (default limit) and risks loss of FX connection*/
	INPUT_VAC_HIGH(4, "Input VAC High"),
	/** AC source’s voltage is under 108VAC**** (default limit) and risks loss of FX connection*/
	INPUT_VAC_LOW(8, "Input VAC Low"),
	/** AC loads are drawing more current than the rating of the FX allows */
	BUY_AMPS_GT_INPUT_SIZE(16, "Buy Amps > Input size"),
	/** An internal FX temperature sensor is malfunctioning */
	TEMP_SENSOR_FAILED(32, "Temp Sensor failed"),
	/** there is a communication problem between the MATE and the FX*/
	COMM_ERROR(64, "Comm Error"),
	/** the FX’s internal cooling fan is not operating properly*/
	FAN_FAILURE(128, "Fan Failure");

	private final int value;
	private final String name;
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
