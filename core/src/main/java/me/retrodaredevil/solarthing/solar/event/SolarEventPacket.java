package me.retrodaredevil.solarthing.solar.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.event.FXACModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.fx.event.FXAuxStateChangePacket;
import me.retrodaredevil.solarthing.solar.outback.fx.event.FXDayEndPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.event.FXOperationalModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.mx.event.MXDayEndPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.event.MXRawDayEndPacket;

/**
 * A type of packet for solar data that goes into the "event" database. These packets have one of the types defined in {@link SolarEventPacketType}
 */
@JsonSubTypes({
		@JsonSubTypes.Type(FXACModeChangePacket.class),
		@JsonSubTypes.Type(FXDayEndPacket.class),
		@JsonSubTypes.Type(MXDayEndPacket.class),
		@JsonSubTypes.Type(MXRawDayEndPacket.class),
		@JsonSubTypes.Type(FXAuxStateChangePacket.class),
		@JsonSubTypes.Type(FXOperationalModeChangePacket.class),
})
public interface SolarEventPacket extends DocumentedPacket<SolarEventPacketType> {
}
