package me.retrodaredevil.solarthing.packet.fx;


/**
 * The FX Operational Mode
 */
public enum OperationalMode{ // one must be active
	UNKNOWN(-1, "unknown"),
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

	private int value;
	private String name;
	OperationalMode(int value, String name){
		this.value = value;
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	public static OperationalMode getMode(int operatingMode){
		for(OperationalMode mode : values()){
			if(mode.value == operatingMode){
				return mode;
			}
		}
		return UNKNOWN;
	}
}

