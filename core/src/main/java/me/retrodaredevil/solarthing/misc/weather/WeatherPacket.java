package me.retrodaredevil.solarthing.misc.weather;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;
import org.jspecify.annotations.NullMarked;

@JsonSubTypes({
		@JsonSubTypes.Type(TemperaturePacket.class)
})
@NullMarked
public interface WeatherPacket extends TypedDocumentedPacket<WeatherPacketType> {
}
