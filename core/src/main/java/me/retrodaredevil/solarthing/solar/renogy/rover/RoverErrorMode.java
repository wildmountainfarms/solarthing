package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.packets.BitmaskMode;

/**
 * Represents different error modes supported by the Renogy Rover
 * <p>
 * PDU address: 0x0121 and 0x0122, Bytes: 4, all 4 bytes
 */
public enum RoverErrorMode implements BitmaskMode {
//	RESERVED_31("reserved", 1 << 31),
	CHARGE_SHORT_CIRCUIT("Charge MOS short circuit", 1 << 30),
	ANTI_REVERSE_SHORT("Anti-reverse MOS short)", 1 << 29),
	SOLAR_PANEL_REVERSELY_CONNECTED("PV reversely connected", 1 << 28),
	SOLAR_PANEL_WORKING_POINT_OVER_VOLTAGE("PV working point over voltage", 1 << 27),
	SOLAR_PANEL_COUNTER_CURRENT("PV counter current", 1 << 26),
	PV_INPUT_SIDE_OVER_VOLTAGE("PV input side over-voltage", 1 << 25),
	PV_INPUT_SIDE_SHORT_CIRCUIT("PV input side short circuit", 1 << 24),
	PV_INPUT_OVERPOWER("PV input overpower", 1 << 23),
	AMBIENT_TEMP_HIGH("Ambient temp high", 1 << 22),
	CONTROLLER_TEMP_HIGH("Controller temp high", 1 << 21),
	/** Load overpower or load over-current*/
	LOAD_OVER("Load over", 1 << 20),
	LOAD_SHORT_CIRCUIT("Load short circuit",  1 << 19), // E4
	BATTERY_UNDER_VOLTAGE("Battery under-voltage", 1 << 18), // E3
	BATTERY_OVER_VOLTAGE("Battery over-voltage", 1 << 17), // E2
	BATTERY_OVER_DISCHARGE("Battery over-discharge", 1 << 16) // E1
	// Bit 15 down to 0 are reserved.
	;
	
	private final String name;
	private final int value;
	
	RoverErrorMode(String name, int value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public int getMaskValue() {
		return value;
	}
	
	@Override
	public String getModeName() {
		return name;
	}
}
