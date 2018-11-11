package me.retrodaredevil.solarthing.packet.mxfm;


import me.retrodaredevil.solarthing.packet.CodeMode;

/**
 * The charger mode of the MXFM
 */
public enum ChargerMode implements CodeMode {
	UNKNOWN(-1, "unknown"),
	SILENT(0, "Silent"),
	FLOAT(1, "Float"),
	BULK(2, "Bulk"),
	ABSORB(3, "Absorb"),
	EQ(4, "EQ");

	private final int value;
	private final String name;

	ChargerMode(int value, String name){
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

	public static ChargerMode getMode(int chargerMode){
		for(ChargerMode mode : values()){
			if(mode.value == chargerMode){
				return mode;
			}
		}
		return UNKNOWN;
	}

}

