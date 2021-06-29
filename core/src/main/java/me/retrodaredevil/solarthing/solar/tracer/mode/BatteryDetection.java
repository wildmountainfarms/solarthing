package me.retrodaredevil.solarthing.solar.tracer.mode;

import com.fasterxml.jackson.annotation.JsonCreator;
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

	@JsonCreator
	public static BatteryDetection parseFromString(String detection) {
		switch (detection) {
			case "auto": return AUTO;
			case "12": return V12;
			case "24": return V24;
		}
		throw new IllegalArgumentException("Unknown detection: " + detection);
	}
}
