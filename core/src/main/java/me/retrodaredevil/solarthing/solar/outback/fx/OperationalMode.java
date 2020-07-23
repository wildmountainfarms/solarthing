package me.retrodaredevil.solarthing.solar.outback.fx;


import me.retrodaredevil.solarthing.packets.CodeMode;

/**
 * The FX Operational Mode
 * <p>
 * Note that modes such as {@link #CHARGE}, {@link #FLOAT} and {@link #EQ} do not tell you if the FX is in a constant voltage charging state. You can use
 * {@link me.retrodaredevil.solarthing.solar.outback.fx.charge.FXChargingMode} and {@link me.retrodaredevil.solarthing.solar.outback.fx.charge.FXChargingStateHandler} to
 * determine this.
 */
public enum OperationalMode implements CodeMode { // one must be active
	INV_OFF(0, "Inv Off"),
	SEARCH(1, "Search"),
	INV_ON(2, "Inv On"),
	/** Refers to a bulk - absorption cycle */
	CHARGE(3, "Charge"),
	/**
	 * Refers to a user programmed end of charge behavior
	 * <p>
	 * For some systems (maybe all systems?), this means that the FX is no longer charging the battery, but is passing all the AC power thru to support the load
	 */
	SILENT(4, "Silent"),
	/** Refers to a user programmed end of charge behavior */
	FLOAT(5, "Float"),
	/** User initiated charge state. Refers to a bulk - EQ cycle, similar to {@link #CHARGE}, but with the EQ voltage and time period */
	EQ(6, "EQ"),
	/**
	 *  Active when the user manually turned the charger off
	 *  <p>
	 *  NOTE: Very old Mate firmwares may use this to report errors (incompatibility with older Mates)
	 */
	CHARGER_OFF(7, "Charger Off"),
	/**
	 * When the FX is drawing power from the batteries to support the AC source it is connected to.
	 * <p>
	 * Grid tie inverters will display this whenever power being removed from the batteries does not exceed the AC loads of the system.
	 * <p>
	 * Basically when this is active it's converting all of the buy current to AC and also has a load on the battery
	 */
	SUPPORT(8, "Support"),
	/**
	 * The FX is exporting more power than it has AC loads
	 */
	SELL_ENABLED(9, "Sell Enabled"),
	/**
	 * This means that the FX's converter is off. This FX is only passing thru the AC from its AC input.
	 * <p>
	 * This happens before the FX starts selling
	 */
	PASS_THRU(10, "Pass Thru"),

	/**
	 * This means that the FX has shut down based on some {@link FXErrorMode}
	 */
	FX_ERROR(90, "FX Error"),
	/**
	 * Means that the Mate has either tried to automatically start the generator in Advanced Gen Start mode and failed, or that the gen started then stopped unexpectedly
	 */
	AGS_ERROR(91, "AGS Error"),
	/**
	 * Means that the Mate has lost communications with one or more of the OutBack devices connected to it
	 * <p>
	 * This is not used on older Mates
	 */
	COM_ERROR(92, "Com Error");

	private final int value;
	private final String name;

	OperationalMode(int value, String name){
		this.value = value;
		this.name = name;
	}
	@Override
	public int getValueCode() {
		return value;
	}

	@Override
	public String getModeName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

}

