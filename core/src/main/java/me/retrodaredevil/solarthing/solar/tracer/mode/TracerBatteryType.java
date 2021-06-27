package me.retrodaredevil.solarthing.solar.tracer.mode;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.retrodaredevil.solarthing.packets.CodeMode;

public enum TracerBatteryType implements CodeMode {
	USER(0, "User"),
	SEALED(1, "Sealed"),
	GEL(2, "GEL"),
	FLOODED(3, "Flooded"),
	;
	private final int value;
	private final String name;

	TracerBatteryType(int value, String name) {
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
	public static TracerBatteryType parse(Object object) {
		if (object instanceof Integer) {
			int code = (int) object;
			for (TracerBatteryType type : values()) {
				if (type.isActive(code)) {
					return type;
				}
			}
			throw new IllegalArgumentException("Unknown code: " + code);
		}
		if (object instanceof String) {
			return parseFromString((String) object);
		}
		throw new IllegalArgumentException("Unknown type: " + object.getClass());
	}

	public static TracerBatteryType parseFromString(String batteryType) {
		for (TracerBatteryType type : values()) {
			if (type.name.equalsIgnoreCase(batteryType)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Unknown type: " + batteryType);
	}
}
