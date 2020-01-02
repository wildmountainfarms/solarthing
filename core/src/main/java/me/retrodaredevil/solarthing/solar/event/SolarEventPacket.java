package me.retrodaredevil.solarthing.solar.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.event.FXACModeChangePacket;

@JsonSubTypes({
		@JsonSubTypes.Type(FXACModeChangePacket.class)
})
public interface SolarEventPacket extends DocumentedPacket<SolarEventPacketType> {
}
