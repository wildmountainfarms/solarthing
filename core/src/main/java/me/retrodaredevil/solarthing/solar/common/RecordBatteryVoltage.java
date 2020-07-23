package me.retrodaredevil.solarthing.solar.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;

public interface RecordBatteryVoltage extends Identifiable {
	@JsonProperty("dailyMinBatteryVoltage")
	float getDailyMinBatteryVoltage();
	@JsonProperty("dailyMaxBatteryVoltage")
	float getDailyMaxBatteryVoltage();
}
