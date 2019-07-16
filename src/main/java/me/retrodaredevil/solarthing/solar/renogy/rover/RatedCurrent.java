package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.packets.CodeMode;

/**
 * The rated charging current: PDU address: 0x000A, Bytes: 2, lower 8 bits
 * <p>
 * The rated discharging current: PDU address: 0x000B, Bytes: 2, upper 8 bits
 */
public enum RatedCurrent implements CodeMode {
	A10(10),
	A20(20),
	A30(30),
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
