package me.retrodaredevil.solarthing.solar.renogy;

import me.retrodaredevil.solarthing.packets.CodeMode;

/**
 * The battery type
 * <p><p>
 * PDU address: 0xE004, Bytes: 2
 * <p>
 * Note: {@link BatteryType#USER} and {@link BatteryType#SELF_CUSTOMIZED} represent the same thing but, {@link BatteryType#USER}
 * is used on Rover Li models
 */
public enum BatteryType implements CodeMode {
	/**
	 * Although this isn't documented, this is used for the USER type on Rover Li 60A
	 */
	USER("user", 0),
	/**
	 * AKA Flooded
	 */
	OPEN("open", 1),
	SEALED("sealed", 2),
	GEL("gel", 3),
	LITHIUM("lithium", 4),
	/**
	 * AKA User
	 */
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
