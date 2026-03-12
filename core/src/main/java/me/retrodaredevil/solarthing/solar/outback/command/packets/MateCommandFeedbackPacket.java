package me.retrodaredevil.solarthing.solar.outback.command.packets;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.marker.EventPacket;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;
import org.jspecify.annotations.NullMarked;

@JsonSubTypes({
		@JsonSubTypes.Type(SuccessMateCommandPacket.class)
})
@NullMarked
public interface MateCommandFeedbackPacket extends TypedDocumentedPacket<MateCommandFeedbackPacketType>, EventPacket {
}
