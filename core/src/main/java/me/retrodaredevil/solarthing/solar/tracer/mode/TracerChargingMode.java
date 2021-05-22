package me.retrodaredevil.solarthing.solar.tracer.mode;

import me.retrodaredevil.solarthing.packets.CodeMode;

/**
 * @deprecated The values of this enum may not be complete
 */
@Deprecated
public enum TracerChargingMode implements CodeMode {
	PWM(1, "PWM")
	;
	private final int value;
	private final String name;

	TracerChargingMode(int value, String name) {
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
