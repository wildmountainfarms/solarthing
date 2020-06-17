package me.retrodaredevil.solarthing.solar.extra;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.solar.SolarPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;

@JsonSubTypes({
		@JsonSubTypes.Type(DailyFXPacket.class),
})
public interface SolarExtraPacket extends SolarPacket<SolarExtraPacketType> {
}
