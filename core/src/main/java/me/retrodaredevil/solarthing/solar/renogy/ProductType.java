package me.retrodaredevil.solarthing.solar.renogy;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.CodeMode;

/**
 * The product type
 *
 * PDU address: 0x000B, Bytes: 2, lower 8 bits
 */
public enum ProductType implements CodeMode {
	CONTROLLER("Controller", 0),
	INVERTER("Inverter", 1)
	;

	private final String name;
	private final int value;

	ProductType(String name, int value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public int getValueCode() {
		return value;
	}

	@Override
	public @NotNull String getModeName() {
		return name;
	}
}
