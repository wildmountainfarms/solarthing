package me.retrodaredevil.solarthing.misc.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface Temperature {
	@JsonProperty("temperatureCelsius")
	float getTemperatureCelsius();
	@JsonProperty("temperatureFahrenheit")
	float getTemperatureFahrenheit();
}
