package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import org.jspecify.annotations.NonNull;

public interface ControllerTemperature {
	@GraphQLInclude("controllerTemperatureCelsius")
	@NonNull Number getControllerTemperatureCelsius();

	@GraphQLInclude("controllerTemperatureFahrenheit")
	default float getControllerTemperatureFahrenheit() {
		return getControllerTemperatureCelsius().floatValue() * 9 / 5.0f + 32;
	}
}
