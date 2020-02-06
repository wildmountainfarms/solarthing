package me.retrodaredevil.solarthing.solar.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Represents something that detects and measures the battery voltage
 */
public interface BatteryVoltage {

	/**
	 * Should be serialized as "batteryVoltage"
	 * @return The battery voltage as a float
	 */
	@JsonProperty("batteryVoltage")
	@JsonPropertyDescription("The battery voltage")
	float getBatteryVoltage();

}
