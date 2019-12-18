package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.packets.CodeMode;


/**
 * The street light status
 * <p><p>
 * PDU address: 0x0120, Bytes: 2, upper 8 bits
 * <p><p>
 * For write (on/off): PDU address: 0x10A
 * <p><p>
 * For write (brightness): PDU address: 0xE001
 */
public enum StreetLight implements CodeMode {
	OFF("Off", 0),
	ON("On", 1 << 7)
	;
	public static final int IGNORED_BITS = 0b01111111;
	private final String name;
	private final int code;
	
	StreetLight(String name, int code) {
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
	
	/**
	 * @param rawValue The raw value read from the register
	 * @return An int in range [0..100] representing the brightness level
	 */
	public static int getBrightnessValue(int rawValue){
		return ~0b10000000 & rawValue;
	}
}
