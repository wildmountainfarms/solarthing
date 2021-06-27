package me.retrodaredevil.solarthing.solar.tracer.mode;

import me.retrodaredevil.solarthing.packets.CodeMode;

public enum TracerBatteryType implements CodeMode {
	USER(0, "User"),
	SEALED(1, "Sealed"),
	GEL(2, "GEL"),
	FLOODED(3, "Flooded"),
	;
	private final int value;
	private final String name;

	TracerBatteryType(int value, String name) {
		this.value = value;
		this.name = name;
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
