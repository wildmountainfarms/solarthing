package me.retrodaredevil.solarthing.misc.weather;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;

@JsonSubTypes({
		@JsonSubTypes.Type(TemperaturePacket.class)
})
public interface WeatherPacket extends TypedDocumentedPacket<WeatherPacketType> {
}
