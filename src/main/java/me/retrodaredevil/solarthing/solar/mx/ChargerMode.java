package me.retrodaredevil.solarthing.solar.mx;


import me.retrodaredevil.solarthing.packets.CodeMode;

/**
 * The charger mode of the MX
 */
@SuppressWarnings("unused")
public enum ChargerMode implements CodeMode {
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

}

