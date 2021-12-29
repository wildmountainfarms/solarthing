package me.retrodaredevil.solarthing.solar.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.marker.EventPacket;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.event.*;
import me.retrodaredevil.solarthing.solar.outback.mx.event.MXAuxModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.mx.event.MXChargerModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.mx.event.MXErrorModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.mx.event.MXRawDayEndPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.event.RoverChargingStateChangePacket;

/**
 * A type of packet for solar data that goes into the "event" database. These packets have one of the types defined in {@link SolarEventPacketType}
 */
@JsonSubTypes({
		@JsonSubTypes.Type(FXACModeChangePacket.class),
		@JsonSubTypes.Type(MXRawDayEndPacket.class),
		@JsonSubTypes.Type(FXAuxStateChangePacket.class),
		@JsonSubTypes.Type(FXOperationalModeChangePacket.class),
		@JsonSubTypes.Type(MXAuxModeChangePacket.class),
		@JsonSubTypes.Type(MXErrorModeChangePacket.class),
		@JsonSubTypes.Type(MXChargerModeChangePacket.class),
		@JsonSubTypes.Type(FXErrorModeChangePacket.class),
		@JsonSubTypes.Type(FXWarningModeChangePacket.class),

		@JsonSubTypes.Type(RoverChargingStateChangePacket.class),
})
public interface SolarEventPacket extends TypedDocumentedPacket<SolarEventPacketType>, EventPacket {
}
