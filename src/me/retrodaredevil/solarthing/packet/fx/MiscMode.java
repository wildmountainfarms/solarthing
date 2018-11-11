package me.retrodaredevil.solarthing.packet.fx;


/**
 * The misc modes for the FX
 */
public enum MiscMode{ // multiple can be active (or 0)
	FX_230V_UNIT(1, "230V unit - voltages * 2 and currents / 2"),
	AUX_OUTPUT_ON(128, "AUX output ON");

	private int value;
	private String name;
	MiscMode(int value, String name){
		this.value = value;
		this.name = name;
	}
	@Override
	public String toString() {
		return name;
	}
	public boolean isActive(int miscValue){
		return (miscValue & value) != 0;
	}
}

