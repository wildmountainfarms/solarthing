package me.retrodaredevil.solarthing.solar.renogy;

import me.retrodaredevil.solarthing.packets.CodeMode;

import static java.util.Objects.requireNonNull;

/**
 * The max voltage supported by the system
 *
 * PDU address: 0x000A, Bytes: 2, upper 8 bits
 */
public enum Voltage implements CodeMode {
	V12(12, 12),
	V24(24, 24),
	V36(36, 36),
	V48(48, 48),
	V96(96, 96),
	AUTO(255, null)
	;
	private final int code;
	private final Integer voltage;
	
	Voltage(int code, Integer voltage) {
		this.code = code;
		this.voltage = voltage;
	}
	
	@Override
	public int getValueCode() {
		return code;
	}
	
	@Override
	public String getModeName() {
		return voltage != null ? (voltage + "V") : "Auto";
	}
	
	public boolean isSupported(Voltage maxVoltage){
		requireNonNull(maxVoltage);
		if(maxVoltage == AUTO){
			throw new IllegalArgumentException("maxVoltage cannot be 'AUTO'");
		}
		if(this == AUTO){
			return true;
		}
		return voltage <= maxVoltage.voltage;
	}
}
