package me.retrodaredevil.solarthing.solar.tracer.mode;

import me.retrodaredevil.solarthing.packets.CodeMode;

public enum TracerChargingType implements CodeMode {
	PWM(1, "PWM")
	// TODO I assume we will have to define a MPPT enum value
	;
	private final int value;
	private final String name;

	TracerChargingType(int value, String name) {
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
