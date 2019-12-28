package me.retrodaredevil.solarthing.solar.outback.command.packets;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;

@JsonSubTypes({
		@JsonSubTypes.Type(SuccessMateCommandPacket.class)
})
public interface MateCommandFeedbackPacket extends DocumentedPacket<MateCommandFeedbackPacketType> {
}
