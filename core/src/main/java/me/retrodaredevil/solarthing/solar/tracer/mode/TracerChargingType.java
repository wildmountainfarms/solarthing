package me.retrodaredevil.solarthing.solar.tracer.mode;

import me.retrodaredevil.solarthing.packets.CodeMode;

/**
 * Represents what type of controller this is, doesn't actually tell you about if the controller is charging or not
 */
public enum TracerChargingType implements CodeMode {
	PWM(1, "PWM"),
	MPPT(2, "MPPT"),
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
