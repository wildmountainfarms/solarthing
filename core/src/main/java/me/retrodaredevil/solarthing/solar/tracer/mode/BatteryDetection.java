package me.retrodaredevil.solarthing.solar.tracer.mode;

import me.retrodaredevil.solarthing.packets.CodeMode;

public enum BatteryDetection implements CodeMode {
	AUTO(0, "Auto"),
	V12(1, "12V"),
	V24(2, "24V"),
	;
	private final int value;
	private final String name;

	BatteryDetection(int value, String name) {
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
