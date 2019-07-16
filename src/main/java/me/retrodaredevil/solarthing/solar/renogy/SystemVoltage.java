package me.retrodaredevil.solarthing.solar.renogy;

import me.retrodaredevil.solarthing.packets.CodeMode;

/**
 * For use with PDU Address 0xE02D, lower 8 bits, (b1 to b0)
 */
public enum SystemVoltage implements CodeMode {
	V12(12, 0),
	V24(24, 1)
	;
	
	private final int voltage;
	private final int code;
	
	SystemVoltage(int voltage, int code) {
		this.voltage = voltage;
		this.code = code;
	}
	
	@Override
	public int getValueCode() {
		return code;
	}
	
	@Override
	public String getModeName() {
		return voltage + "V";
	}
}
