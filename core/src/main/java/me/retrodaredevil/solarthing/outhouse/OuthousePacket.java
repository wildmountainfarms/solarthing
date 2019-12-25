package me.retrodaredevil.solarthing.outhouse;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;

@JsonSubTypes({
		@JsonSubTypes.Type(OccupancyPacket.class),
		@JsonSubTypes.Type(WeatherPacket.class)
})
public interface OuthousePacket extends DocumentedPacket<OuthousePacketType> {
}
