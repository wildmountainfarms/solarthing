package me.retrodaredevil.solarthing.solar.outback.mx;

import me.retrodaredevil.solarthing.packets.CodeMode;

/**
 * NOTE: Some of these values are only supported on FLEXmax80 and FLEXmax60. If it is a FLEXmax unit, you also have to
 * make sure that the value code you compare with this
 */
@SuppressWarnings("unused")
public enum AuxMode implements CodeMode {
	DISABLED(0, "disabled"),
	DIVERSION(1, "Diversion"),
	REMOTE(2, "Remote"),
	MANUAL(3, "Manual"),
	VENT_FAN(4, "Vent Fan"),
	PV_TRIGGER(5, "PV Trigger"),
	
	/** Only used on FLEXmax80/FLEXmax60*/
	FLOAT(6, "Float"),
	/** Only used on FLEXmax80/FLEXmax60*/
	ERROR_OUTPUT(7, "ERROR Output"),
	/** Only used on FLEXmax80/FLEXmax60*/
	NIGHT_LIGHT(8, "Night Light"),
	/** Only used on FLEXmax80/FLEXmax60*/
	PWM_DIVERSION(9, "PWM Diversion"),
	/** Only used on FLEXmax80/FLEXmax60*/
	LOW_BATTERY(10, "Low Battery");

	private final int value;
	private final String name;
	AuxMode(int value, String name){
		this.value = value;
		this.name = name;
	}

	@Override
	public String getModeName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int getValueCode() {
		return value;
	}
	
	@Override
	public int getIgnoredBits() {
		return 0b01000000;
	}
	
	/**
	 * Checks to see if bit 7 is set. Only works on FLEXmax80 and FLEXmax60
	 * @param valueCode The value code
	 * @return true if aux mode is active, false otherwise
	 */
	public static boolean isAuxModeActive(int valueCode){
		return (valueCode & 0b01000000) != 0; // if bit 7 is set
	}
	public static int getActualValueCode(int valueCode){
		return valueCode & 0b00111111; // the lower 6 bits
	}

}

