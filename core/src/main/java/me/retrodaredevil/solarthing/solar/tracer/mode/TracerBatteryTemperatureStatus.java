package me.retrodaredevil.solarthing.solar.tracer.mode;

import me.retrodaredevil.solarthing.packets.CodeMode;

public enum TracerBatteryTemperatureStatus implements CodeMode {
	NORMAL(0, "Normal"),
	OVER_TEMPERATURE(1, "Over Temperature"),
	LOW_TEMPERATURE(2, "Low Temperature"),
	;

	private final int value;
	private final String name;

	TracerBatteryTemperatureStatus(int value, String name) {
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
