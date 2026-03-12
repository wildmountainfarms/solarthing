package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ControllerTemperature {
	// TODO remove NonNull
	@GraphQLInclude("controllerTemperatureCelsius")
	@NonNull Number getControllerTemperatureCelsius();

	@GraphQLInclude("controllerTemperatureFahrenheit")
	default float getControllerTemperatureFahrenheit() {
		return getControllerTemperatureCelsius().floatValue() * 9 / 5.0f + 32;
	}
}
