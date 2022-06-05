package me.retrodaredevil.solarthing.solar.tracer.mode;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.CodeMode;

public enum InputVoltageStatus implements CodeMode {
	NORMAL(0, "Normal"),
	NOT_CONNECTED(1, "Not Connected"),
	HIGH_VOLTAGE(2, "High Voltage"),
	INPUT_ERROR(3, "Input Error"),
	;
	private final int value;
	private final String name;

	InputVoltageStatus(int value, String name) {
		this.value = value;
		this.name = name;
	}

	@Override
	public int getValueCode() {
		return value;
	}

	@Override
	public @NotNull String getModeName() {
		return name;
	}
}
