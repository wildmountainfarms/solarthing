package me.retrodaredevil.solarthing.solar.common;

/**
 * Represents something that detects and measures the battery voltage
 */
public interface BatteryVoltage {
	
	/**
	 * Should be serialized as "batteryVoltage"
	 * @return The battery voltage as a float
	 */
	float getBatteryVoltage();
	
}
