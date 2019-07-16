package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.packets.CodeMode;

/**
 * Enum values and names subject to change
 * <p><p>
 * R/W PDU address: 0xE01D, Bytes: 2
 */
public enum LoadWorkingMode implements CodeMode {
	LIGHT_CONTROL(0, null),
	LIGHT_DELAY_1(1, 1),
	LIGHT_DELAY_2(2, 2),
	LIGHT_DELAY_3(3, 3),
	LIGHT_DELAY_4(4, 4),
	LIGHT_DELAY_5(5, 5),
	LIGHT_DELAY_6(6, 6),
	LIGHT_DELAY_7(7, 7),
	LIGHT_DELAY_8(8, 8),
	LIGHT_DELAY_9(9, 9),
	LIGHT_DELAY_10(10, 10),
	LIGHT_DELAY_11(11, 11),
	LIGHT_DELAY_12(12, 12),
	LIGHT_DELAY_13(13, 13),
	LIGHT_DELAY_14(14, 14),
	MANUAL(15, null),
	DEBUG(16, null),
	NORMAL(17, null),
	;
	private final int value;
	private final Integer timeDelay;
	
	LoadWorkingMode(int value, Integer timeDelay) {
		this.value = value;
		this.timeDelay = timeDelay;
	}
	public Integer getTimeDelay(){
		return timeDelay;
	}
	
	@Override
	public int getValueCode() {
		return value;
	}
	
	@Override
	public String getModeName() {
		return toString();
	}
}
