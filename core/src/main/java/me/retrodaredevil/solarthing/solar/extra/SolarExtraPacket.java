package me.retrodaredevil.solarthing.solar.extra;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.marker.StatusPacket;
import me.retrodaredevil.solarthing.solar.SolarPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;
import org.jspecify.annotations.NullMarked;

@JsonSubTypes({
		@JsonSubTypes.Type(DailyFXPacket.class),
})
@NullMarked
public interface SolarExtraPacket extends SolarPacket<SolarExtraPacketType>, StatusPacket {
}
