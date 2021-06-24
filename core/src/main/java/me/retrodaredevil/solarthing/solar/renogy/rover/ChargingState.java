package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.CodeMode;
import me.retrodaredevil.solarthing.solar.common.SolarMode;
import me.retrodaredevil.solarthing.solar.common.SolarModeType;

/**
 * The charging state
 *
 * PDU address: 0x0120, Bytes: 2, lower 8 bits
 */
public enum ChargingState implements CodeMode, SolarMode {
	DEACTIVATED("Deactivated", 0, SolarModeType.CHARGE_CONTROLLER_OFF),
	ACTIVATED("Activated", 1, SolarModeType.BULK),
	MPPT("MPPT charging", 2, SolarModeType.BULK),
	EQ("EQ", 3, SolarModeType.EQUALIZE),
	/** AKA lifting. Involves a high current for a short period. This is usually called Absorb on other charge controllers*/
	BOOST("Boost", 4, SolarModeType.ABSORB),
	/** Charges at a reduced voltage, as it charges, the current is gradually reduced*/
	FLOAT("Float", 5, SolarModeType.FLOAT),
	/** AKA overpower. Done with a constant current. This is only done to lithium batteries in place of the {@link #BOOST} state*/
	CURRENT_LIMITING("Current limiting", 6, SolarModeType.CURRENT_LIMITING),
//	KEEP("Keep 7", 7),
	/** AKA Generator. Direct charge mode is only used on Dual Input Renogy products. This means battery is being charged by alternator */
	DIRECT_CHARGE("Direct Charge", 8, SolarModeType.DIRECT_CHARGE), // on DCC Charge Controller only
	;

	private final String name;
	private final int code;
	private final SolarModeType solarModeType;

	ChargingState(String name, int code, SolarModeType solarModeType) {
		this.name = name;
		this.code = code;
		this.solarModeType = solarModeType;
	}

	@Override
	public int getValueCode() {
		return code;
	}

	@Override
	public String getModeName() {
		return name;
	}

	@Override
	public @NotNull SolarModeType getSolarModeType() {
		return solarModeType;
	}
}
