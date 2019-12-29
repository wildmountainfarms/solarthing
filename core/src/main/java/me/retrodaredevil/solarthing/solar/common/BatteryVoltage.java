package me.retrodaredevil.solarthing.solar.common;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents something that detects and measures the battery voltage
 */
public interface BatteryVoltage {
	
	/**
	 * Should be serialized as "batteryVoltage"
	 * @return The battery voltage as a float
	 */
	@JsonProperty("batteryVoltage")
	float getBatteryVoltage();
	
}
