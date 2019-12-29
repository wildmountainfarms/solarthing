package me.retrodaredevil.solarthing.solar.extra;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.solar.SolarPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.extra.DailyMXPacket;

@JsonSubTypes({
		@JsonSubTypes.Type(DailyFXPacket.class),
		@JsonSubTypes.Type(DailyMXPacket.class),
})
public interface SolarExtraPacket extends SolarPacket<SolarExtraPacketType> {
}
