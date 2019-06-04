package me.retrodaredevil.solarthing.solar.fx;


import me.retrodaredevil.solarthing.packets.BitmaskMode;

/**
 * The misc modes for the FX
 */
public enum MiscMode implements BitmaskMode {
	/**
	 * Represents a 230V FX unit. This means multiply input and output voltages by 2 and divide all currents by 2
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

