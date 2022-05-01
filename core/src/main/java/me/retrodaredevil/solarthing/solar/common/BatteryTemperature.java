package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.annotations.NotNull;

public interface BatteryTemperature {
	@GraphQLInclude("batteryTemperatureCelsius")
	@NotNull Number getBatteryTemperatureCelsius();

	@GraphQLInclude("batteryTemperatureFahrenheit")
	default float getBatteryTemperatureFahrenheit() {
		return getBatteryTemperatureCelsius().floatValue() * 9 / 5.0f + 32;
	}
}
