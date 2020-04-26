package me.retrodaredevil.solarthing.solar.extra;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.solar.SolarPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.charge.FXChargingPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.extra.DailyMXPacket;

@JsonSubTypes({
		@JsonSubTypes.Type(DailyFXPacket.class),
		@JsonSubTypes.Type(DailyMXPacket.class),
		@JsonSubTypes.Type(FXChargingPacket.class),
		@JsonSubTypes.Type(DailyUpdatePacket.class),
})
public interface SolarExtraPacket extends SolarPacket<SolarExtraPacketType> {
}
