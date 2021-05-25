package me.retrodaredevil.solarthing.solar.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;

public interface RecordBatteryVoltage extends Identifiable {
	@JsonProperty("dailyMinBatteryVoltage")
	@JsonPropertyDescription("The minimum battery voltage for the day. Note this may reset at a different time compared to max battery voltage for the day.")
	float getDailyMinBatteryVoltage();

	@JsonProperty("dailyMaxBatteryVoltage")
	@JsonPropertyDescription("The maximum battery voltage for the day. Note this may reset at a different time compared to min battery voltage for the day.")
	float getDailyMaxBatteryVoltage();
}
