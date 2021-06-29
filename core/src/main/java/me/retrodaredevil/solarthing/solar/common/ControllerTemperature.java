package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.annotations.NotNull;

public interface ControllerTemperature {
	@GraphQLInclude("controllerTemperatureCelsius")
	@NotNull Number getControllerTemperatureCelsius();

	@GraphQLInclude("controllerTemperatureFahrenheit")
	default float getControllerTemperatureFahrenheit() {
		return getControllerTemperatureCelsius().floatValue() * 9 / 5.0f + 32;
	}
}
