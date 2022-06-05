package me.retrodaredevil.solarthing.solar.outback.fx;


import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.CodeMode;
import me.retrodaredevil.solarthing.solar.common.SolarMode;
import me.retrodaredevil.solarthing.solar.common.SolarModeType;

import static me.retrodaredevil.solarthing.solar.common.SolarModeType.*;

/**
 * The FX Operational Mode
 * <p>
 * Note that modes such as {@link #CHARGE}, {@link #FLOAT} and {@link #EQ} do not tell you if the FX is in a constant voltage charging state. You can use
 * {@link me.retrodaredevil.solarthing.solar.outback.fx.charge.FXChargingMode} and {@link me.retrodaredevil.solarthing.solar.outback.fx.charge.FXChargingStateHandler} to
 * determine this.
 */
public enum OperationalMode implements CodeMode, SolarMode { // one must be active
	INV_OFF(0, "Inv Off", INVERTER_OFF),
	SEARCH(1, "Search", INVERTER_SEARCH),
	INV_ON(2, "Inv On", INVERTER_ON),
	/** Refers to a bulk - absorption cycle */
	CHARGE(3, "Charge", BULK),
	/**
	 * Refers to a user programmed end of charge behavior
	 * <p>
	 * For some systems (maybe all systems?), this means that the FX is no longer charging the battery, but is passing all the AC power thru to support the load
	 */
	SILENT(4, "Silent", INVERTER_SILENT),
	/** Refers to a user programmed end of charge behavior */
	FLOAT(5, "Float", SolarModeType.FLOAT),
	/** User initiated charge state. Refers to a bulk - EQ cycle, similar to {@link #CHARGE}, but with the EQ voltage and time period */
	EQ(6, "EQ", EQUALIZE),
	/**
	 *  Active when the user manually turned the charger off
	 *  <p>
	 *  NOTE: Very old Mate firmwares may use this to report errors (incompatibility with older Mates)
	 */
	CHARGER_OFF(7, "Charger Off", INVERTER_CHARGER_OFF),
	/**
	 * When the FX is drawing power from the batteries to support the AC source it is connected to.
	 * <p>
	 * Grid tie inverters will display this whenever power being removed from the batteries does not exceed the AC loads of the system.
	 * <p>
	 * Basically when this is active it's converting all of the buy current to AC and also has a load on the battery
	 */
	SUPPORT(8, "Support", INVERTER_SUPPORT),
	/**
	 * The FX is exporting more power than it has AC loads
	 */
	SELL_ENABLED(9, "Sell Enabled", INVERTER_SELL),
	/**
	 * This means that the FX's converter is off. This FX is only passing thru the AC from its AC input.
	 * <p>
	 * This happens before the FX starts selling
	 */
	PASS_THRU(10, "Pass Thru", INVERTER_PASS_THRU),

	/**
	 * This means that the FX has shut down based on some {@link FXErrorMode}
	 */
	FX_ERROR(90, "FX Error", INVERTER_UNKNOWN),
	/**
	 * Means that the Mate has either tried to automatically start the generator in Advanced Gen Start mode and failed, or that the gen started then stopped unexpectedly
	 */
	AGS_ERROR(91, "AGS Error", INVERTER_UNKNOWN),
	/**
	 * Means that the Mate has lost communications with one or more of the OutBack devices connected to it
	 * <p>
	 * This is not used on older Mates
	 */
	COM_ERROR(92, "Com Error", INVERTER_UNKNOWN);

	private final int value;
	private final String name;
	private final SolarModeType solarModeType;

	OperationalMode(int value, String name, SolarModeType solarModeType){
		this.value = value;
		this.name = name;
		this.solarModeType = solarModeType;
	}
	@Override
	public int getValueCode() {
		return value;
	}

	@Override
	public @NotNull String getModeName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public @NotNull SolarModeType getSolarModeType() {
		return solarModeType;
	}
}
