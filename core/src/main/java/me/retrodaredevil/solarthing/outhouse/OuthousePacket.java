package me.retrodaredevil.solarthing.outhouse;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;

@JsonSubTypes({
		@JsonSubTypes.Type(value = OccupancyPacket.class),
		@JsonSubTypes.Type(value = WeatherPacket.class)
})
public interface OuthousePacket extends DocumentedPacket<OuthousePacketType> {
}
