package me.retrodaredevil.solarthing.solar.outback.fx;


import me.retrodaredevil.solarthing.packets.CodeMode;

/**
 * The FX Operational Mode
 */
public enum OperationalMode implements CodeMode { // one must be active
	INV_OFF(0, "Inv Off"),
	SEARCH(1, "Search"),
	INV_ON(2, "Inv On"),
	CHARGE(3, "Charge"),
	SILENT(4, "Silent"),
	FLOAT(5, "Float"),
	EQ(6, "EQ"),
	CHARGER_OFF(7, "Charger Off"),
	SUPPORT(8, "Support"),
	SELL_ENABLED(9, "Sell Enabled"),
	PASS_THRU(10, "Pass Thru"),

	FX_ERROR(90, "FX Error"),
	AGS_ERROR(91, "AGS Error"),
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

