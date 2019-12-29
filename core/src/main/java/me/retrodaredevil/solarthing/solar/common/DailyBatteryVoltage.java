package me.retrodaredevil.solarthing.solar.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface DailyBatteryVoltage extends DailyData {
	@JsonProperty("dailyMinBatteryVoltage")
	float getDailyMinBatteryVoltage();
	@JsonProperty("dailyMaxBatteryVoltage")
	float getDailyMaxBatteryVoltage();
}
