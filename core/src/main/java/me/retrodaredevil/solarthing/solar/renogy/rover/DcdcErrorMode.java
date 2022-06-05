package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.annotations.NotNull;

public enum DcdcErrorMode implements SimpleRoverErrorMode {
	BACKUP_BATTERY_OVER_DISCHARGE("Backup battery over discharge", 1),
	BACKUP_BATTERY_OVER_VOLTAGE("Backup battery over pressure", 1 << 1),
	BACKUP_BATTERY_UNDER_VOLTAGE("Backup battery under pressure", 1 << 2), // this is just a warning and is actually pretty common
	// b3 and b4 keep
	CONTROLLER_OVER_TEMPERATURE("Over temperature inside controller", 1 << 5),
	BATTERY_OVER_TEMPERATURE("Backup battery over temperature", 1 << 6),
	PV_POWER_OVERLOAD("PV power overload", 1 << 7),
	FAN_ALARM("Fan alarm", 1 << 8),
	PV_OVER_VOLTAGE("PV over voltage", 1 << 9),
	// b10 and b11 keep
	SOLAR_PANEL_REVERSELY_CONNECTED("PV reversely connected", 1 << 12),
	// b13-b15 keep
	// next register
	// b16-b19 keep
	CONTROLLER_WARM("Controller warm", 1 << 20),
	OVER_CURRENT("Over current", 1 << 21),
	UNABLE_TO_TRANSLATE_B22("Unable to translate b22", 1 << 22),
	UNABLE_TO_TRANSLATE_B23("Unable to translate b23", 1 << 23),
	GENERATOR_OVER_VOLTAGE("Generator over voltage", 1 << 24),
	START_BATTERY_BACKUP("Start battery backup", 1 << 25),
	BMS_OVERCHARGE("BMS overcharge protection", 1 << 26),
	BATTERY_TEMPERATURE_HALT_CHARGING("Backup battery temperature halts charging", 1 << 27), // low battery temperature protection
	// b28-b31 reserved
	;

	private final String name;
	private final int value;

	DcdcErrorMode(String name, int value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public int getMaskValue() {
		return value;
	}

	@Override
	public @NotNull String getModeName() {
		return name;
	}
}
