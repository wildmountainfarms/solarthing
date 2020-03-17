package me.retrodaredevil.solarthing.solar.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.event.*;
import me.retrodaredevil.solarthing.solar.outback.mx.event.*;

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
		@JsonSubTypes.Type(MXAuxModeChangePacket.class),
		@JsonSubTypes.Type(MXErrorModeChangePacket.class),
		@JsonSubTypes.Type(MXChargerModeChangePacket.class),
		@JsonSubTypes.Type(FXErrorModeChangePacket.class),
		@JsonSubTypes.Type(FXWarningModeChangePacket.class),
})
public interface SolarEventPacket extends DocumentedPacket<SolarEventPacketType> {
}
