package me.retrodaredevil.solarthing.solar.tracer.mode;

import me.retrodaredevil.solarthing.packets.CodeMode;
import org.jspecify.annotations.NonNull;

public enum BatteryManagementMode implements CodeMode {
	VOLTAGE_COMPENSATION(0, "Voltage Compensation"),
	SOC(1, "SOC"),
	;
	private final int value;
	private final String name;

	BatteryManagementMode(int value, String name) {
		this.value = value;
		this.name = name;
	}

	@Override
	public int getValueCode() {
		return value;
	}

	@Override
	public @NonNull String getModeName() {
		return name;
	}
}
