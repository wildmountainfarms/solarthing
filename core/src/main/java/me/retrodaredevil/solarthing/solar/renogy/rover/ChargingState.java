package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.packets.CodeMode;

/**
 * The charging state
 *
 * PDU address: 0x0120, Bytes: 2, lower 8 bits
 */
public enum ChargingState implements CodeMode {
	DEACTIVATED("Deactivated", 0),
	ACTIVATED("Activated", 1),
	MPPT("MPPT charging", 2),
	EQ("EQ", 3),
	/** Involves a high current for a short period*/
	BOOST("Boost", 4),
	/** Charges at a reduced voltage, as it charges, the current is gradually reduced*/
	FLOAT("Float", 5),
	/** AKA overpower. Done with a constant current*/
	CURRENT_LIMITING("Current limiting", 6)
	;
	
	private final String name;
	private final int code;
	
	ChargingState(String name, int code) {
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
