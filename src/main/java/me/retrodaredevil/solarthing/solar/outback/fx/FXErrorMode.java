package me.retrodaredevil.solarthing.solar.outback.fx;

import me.retrodaredevil.solarthing.packets.BitmaskMode;

/**
 * The errors modes for the FX
 */
public enum FXErrorMode implements BitmaskMode { // multiple can be active (or 0)
	LOW_VAC_OUTPUT(1, "Low VAC output"),
	STACKING_ERROR(2, "Stacking Error"),
	OVER_TEMP(4, "Over Temp."),
	LOW_BATTERY(8, "Low Battery"),
	PHASE_LOSS(16, "Phase Loss"),
	HIGH_BATTERY(32, "High Battery"),
	SHORTED_OUTPUT(64, "Shorted output"),
	BACK_FEED(128, "Back feed");
	private final int value;
	private final String name;
	FXErrorMode(int value, String name){
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
