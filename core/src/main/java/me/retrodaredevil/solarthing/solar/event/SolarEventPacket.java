package me.retrodaredevil.solarthing.solar.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.event.FXACModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.mx.event.MXDayEndPacket;

/**
 * A type of packet for solar data that goes into the "event" database. These packets have one of the types defined in {@link SolarEventPacketType}
 */
@JsonSubTypes({
		@JsonSubTypes.Type(FXACModeChangePacket.class),
		@JsonSubTypes.Type(MXDayEndPacket.class)
})
public interface SolarEventPacket extends DocumentedPacket<SolarEventPacketType> {
}
