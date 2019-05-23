package me.retrodaredevil.iot.solar.fx;


import me.retrodaredevil.iot.packets.CodeMode;

/**
 * The AC Mode for the FX
 */
public enum ACMode implements CodeMode { // one must be active
	NO_AC(0, "No AC"),
	AC_DROP(1, "AC Drop"),
	AC_USE(2, "AC Use"),
	UNKNOWN(-1, "UNKNOWN");

	private int value;
	private String name;
	ACMode(int value, String name){
		this.value = value;
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int getValueCode() {
		return value;
	}

	@Override
	public String getModeName() {
		return name;
	}
}

