package me.retrodaredevil.solarthing.solar.outback.mx;

import me.retrodaredevil.solarthing.packets.BitmaskMode;

/**
 * NOTE: Error modes are only implemented on MX versions greater than 5.11 and on FLEXmax80 and FLEXmax60s.
 */
public enum MXErrorMode implements BitmaskMode {
	SHORTED_BATTERY_SENSOR(32, "Shorted Battery Sensor"),
	TOO_HOT(64, "Too Hot"),
	HIGH_VOC(128, "High VOC");

	private final int value;
	private final String name;

	MXErrorMode(int value, String name){
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
