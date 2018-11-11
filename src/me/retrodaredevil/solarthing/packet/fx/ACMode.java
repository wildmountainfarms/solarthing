package me.retrodaredevil.solarthing.packet.fx;


/**
 * The AC Mode for the FX
 */
public enum ACMode{ // one must be active
	NO_AC(0, "No AC"),
	AC_DROP(1, "AC Drop"),
	AC_USE(2, "AC Use"),
	UNKNOWN(-1, "UNKNOWN");

	private int value;
	private String name;
	ACMode(int value, String name){
		this.value = value;
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	public static ACMode getACMode(int value){
		for(ACMode mode : values()){
			if(mode.value == value){
				return mode;
			}
		}
		return UNKNOWN;
	}
}

