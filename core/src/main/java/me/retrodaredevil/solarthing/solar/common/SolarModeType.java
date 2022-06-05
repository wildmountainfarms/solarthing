package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.annotations.NotNull;

public enum SolarModeType implements BasicSolarMode {
	INVERTER_OFF("Off"),
	INVERTER_ON("On"),
	INVERTER_SEARCH("Search"),
	INVERTER_SILENT("Silent"),
	INVERTER_SELL("Sell"),
	INVERTER_PASS_THRU("Pass Thru"),
	INVERTER_SUPPORT("Support"),
	INVERTER_CHARGER_OFF("Charger Off"),
	INVERTER_UNKNOWN("Unknown"),

	CHARGE_CONTROLLER_OFF("Off"),

	BULK_ABSORB("Bulk/Absorb"),
	BULK_EQUALIZE("Bulk/EQ"),
	BULK_FLOAT("Bulk/Float"),

	BULK("Bulk"),
	ABSORB("Absorb"),
	EQUALIZE("EQ"),
	FLOAT("Float"),

	CURRENT_LIMITING("Current Limiting"),
	DIRECT_CHARGE("Direct Charge"),
	;

	private final String displayName;

	SolarModeType(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public boolean isActive(int code) {
		return false;
	}

	@Override
	public @NotNull String getModeName() {
		return displayName;
	}

	@Override
	public boolean isOn() {
		return this != INVERTER_OFF && this != CHARGE_CONTROLLER_OFF;
	}
}
