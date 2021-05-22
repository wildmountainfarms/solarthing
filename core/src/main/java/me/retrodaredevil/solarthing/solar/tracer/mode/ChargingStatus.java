package me.retrodaredevil.solarthing.solar.tracer.mode;

import me.retrodaredevil.solarthing.packets.CodeMode;

public enum ChargingStatus implements CodeMode {
	NO_CHARGING(0, "No charging"),
	FLOAT(1, "Float"),
	BOOST(2, "Boost"),
	EQUALIZATION(3, "Equalization"),
	;
	private final int value;
	private final String name;

	ChargingStatus(int value, String name) {
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
