package me.retrodaredevil.solarthing.solar.tracer.mode;

import me.retrodaredevil.solarthing.packets.CodeMode;

public enum TracerBatteryVoltageStatus implements CodeMode {
	NORMAL(0, "Normal"),
	OVER_VOLTAGE(1, "Over voltage"),
	UNDER_VOLTAGE(2, "Under Voltage"),
	LOW_VOLTAGE_DISCONNECT(3, "Low Voltage Disconnect"),
	FAULT(4, "Fault"),
	;
	private final int value;
	private final String name;

	TracerBatteryVoltageStatus(int value, String name) {
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
