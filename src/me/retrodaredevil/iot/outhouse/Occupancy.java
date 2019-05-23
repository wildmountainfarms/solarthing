package me.retrodaredevil.iot.outhouse;

import me.retrodaredevil.iot.packets.CodeMode;

import static java.util.Objects.requireNonNull;

public enum Occupancy implements CodeMode {
	VACANT(0, "vacant"),
	OCCUPIED(1, "occupied")
	;
	
	private final int code;
	private final String name;
	
	Occupancy(int code, String name) {
		this.code = code;
		this.name = requireNonNull(name);
	}
	
	@Override
	public int getValueCode() {
		return code;
	}
	
	@Override
	public String getModeName() {
		return name;
	}
}
