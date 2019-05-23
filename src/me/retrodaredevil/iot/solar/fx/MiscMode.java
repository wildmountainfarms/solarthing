package me.retrodaredevil.iot.solar.fx;


import me.retrodaredevil.iot.packets.BitmaskMode;

/**
 * The misc modes for the FX
 */
public enum MiscMode implements BitmaskMode { // multiple can be active (or 0)
	/**
	 * Represents a 230V FX unit. This means multiple input and output voltages by 2 and divide all currents by 2
	 */
	FX_230V_UNIT(1, "FX - 230V unit"),
	AUX_OUTPUT_ON(128, "AUX output ON");

	private final int value;
	private final String name;
	MiscMode(int value, String name){
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

