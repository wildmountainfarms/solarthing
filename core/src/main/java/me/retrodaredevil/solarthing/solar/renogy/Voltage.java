package me.retrodaredevil.solarthing.solar.renogy;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.CodeMode;
import org.jspecify.annotations.Nullable;

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
	private final @Nullable Integer voltage;

	Voltage(int code, @Nullable Integer voltage) {
		this.code = code;
		this.voltage = voltage;
	}

	@Override
	public int getValueCode() {
		return code;
	}

	@Override
	public @NotNull String getModeName() {
		return voltage != null ? (voltage + "V") : "Auto";
	}

	public boolean isSupported(Voltage maxVoltage){
		requireNonNull(maxVoltage);
		if(maxVoltage.voltage == null){
			throw new IllegalArgumentException("maxVoltage cannot be 'AUTO'");
		}
		if(voltage == null){ // this == AUTO
			return true;
		}
		return voltage <= maxVoltage.voltage;
	}
	public static Voltage from(int value) {
		for (Voltage voltage : values()) {
			if (voltage.voltage != null && voltage.voltage == value) {
				return voltage;
			}
		}
		throw new IllegalArgumentException("Unknown value: " + value);
	}
}
