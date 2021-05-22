package me.retrodaredevil.solarthing.solar.tracer.mode;

import me.retrodaredevil.solarthing.packets.CodeMode;

public enum LoadControlMode implements CodeMode {
	MANUAL_CONTROL(0, "Manual Control"),
	LIGHT_ON_OFF(1, "Light ON/OFF"),
	LIGHT_ON_WITH_TIMER(2, "Light ON+Timer"),
	TIME_CONTROL(3, "Time Control"),
	;

	private final int value;
	private final String name;

	LoadControlMode(int value, String name) {
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
