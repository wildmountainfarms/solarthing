package me.retrodaredevil.solarthing.solar.renogy;

import me.retrodaredevil.solarthing.packets.CodeMode;

/**
 * The rated charging current: PDU address: 0x000A, Bytes: 2, lower 8 bits
 * <p>
 * The rated discharging current: PDU address: 0x000B, Bytes: 2, upper 8 bits
 * @deprecated This was deprecated because it was discovered that an undocumented current was used on a certain controller. Because of this it is recommended not to use this as it may become outdated if another current value is added
 */
@Deprecated
public enum RatedCurrent implements CodeMode {
	A10(10),
	A20(20),
	A30(30),
	A40(40), // Although not documented, this is supported
	A45(45),
	A60(60),
	;
	
	private final int current;
	
	RatedCurrent(int current) {
		this.current = current;
	}
	public int getCurrent(){
		return current;
	}
	
	@Override
	public int getValueCode() {
		return current;
	}
	
	@Override
	public String getModeName() {
		return current + "A";
	}
}
