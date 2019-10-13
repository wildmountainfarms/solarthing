package me.retrodaredevil.solarthing.solar.renogy;

import me.retrodaredevil.solarthing.packets.CodeMode;

/**
 * The street light status
 * <p><p>
 * PDU address: 0xE004, Bytes: 2
 */
public enum BatteryType implements CodeMode {
	/**
	 * AKA Flooded
	 */
	OPEN("open", 1),
	SEALED("sealed", 2),
	GEL("gel", 3),
	LITHIUM("lithium", 4),
	SELF_CUSTOMIZED("self-customized", 5)
	;
	
	private final String name;
	private final int code;
	
	BatteryType(String name, int code) {
		this.name = name;
		this.code = code;
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
