package me.retrodaredevil.solarthing.misc.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.misc.common.DataIdentifiable;

@JsonExplicit
@JsonTypeName("TEMPERATURE")
public interface TemperaturePacket extends WeatherPacket, DataIdentifiable {
	@Override
	default @NotNull WeatherPacketType getPacketType() {
		return WeatherPacketType.TEMPERATURE;
	}
	@JsonProperty("temperatureCelsius")
	float getTemperatureCelsius();
	@JsonProperty("temperatureFahrenheit")
	float getTemperatureFahrenheit();
}
