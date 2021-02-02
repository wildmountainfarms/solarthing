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
	/** AKA lifting. Involves a high current for a short period. This is usually called Absorb on other charge controllers*/
	BOOST("Boost", 4),
	/** Charges at a reduced voltage, as it charges, the current is gradually reduced*/
	FLOAT("Float", 5),
	/** AKA overpower. Done with a constant current. This is only done to lithium batteries in place of the {@link #BOOST} state*/
	CURRENT_LIMITING("Current limiting", 6),
//	KEEP("Keep 7", 7),
	/** AKA Generator. Direct charge mode is only used on newer Renogy products. */
	DIRECT_CHARGE("Direct Charge", 8), // on DCC Charge Controller only
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
