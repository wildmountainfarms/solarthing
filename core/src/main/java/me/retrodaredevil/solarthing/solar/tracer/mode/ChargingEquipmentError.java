package me.retrodaredevil.solarthing.solar.tracer.mode;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.BitmaskMode;

public enum ChargingEquipmentError implements BitmaskMode {
	CHARGING_MOSFET_SHORT(1 << 13, "Charging MOSFET is short"),
	CHARGING_OR_ANTI_REVERSE_MOSFET_SHORT(1 << 12, "Charging or Anti-reverse MOSFET is short"),
	ANTI_REVERSE_MOSFET_SHORT(1 << 11, "Anti-reverse MOSFET is short"),
	INPUT_OVER_CURRENT(1 << 10, "Input is over current"),
	LOAD_OVER_CURRENT(1 << 9, "The load is Over current"),
	LOAD_SHORT(1 << 8, "The load is short"),
	LOAD_MOSFET_SHORT(1 << 7, "Load MOSFET is short"),
	PV_INPUT_SHORT(1 << 4, "PV Input is short"),
	FAULT(1 << 1, "Fault"),
	;

	private final int maskValue;
	private final String name;

	ChargingEquipmentError(int maskValue, String name) {
		this.maskValue = maskValue;
		this.name = name;
	}

	@Override
	public int getMaskValue() {
		return maskValue;
	}

	@Override
	public @NotNull String getModeName() {
		return name;
	}
}
