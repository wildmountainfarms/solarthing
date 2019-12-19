package me.retrodaredevil.solarthing.solar.common;

public interface DailyBatteryVoltage extends DailyData {
	float getDailyMinBatteryVoltage();
	float getDailyMaxBatteryVoltage();
}
