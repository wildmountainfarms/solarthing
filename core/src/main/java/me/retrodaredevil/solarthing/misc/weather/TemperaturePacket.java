package me.retrodaredevil.solarthing.misc.weather;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.misc.common.AddressedData;
import me.retrodaredevil.solarthing.misc.common.Temperature;

@JsonExplicit
@JsonTypeName("TEMPERATURE")
public interface TemperaturePacket extends WeatherPacket, Temperature, AddressedData {
	@Override
	default @NotNull WeatherPacketType getPacketType() {
		return WeatherPacketType.TEMPERATURE;
	}
}
