package me.retrodaredevil.solarthing.type.event.feedback;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.marker.EventPacket;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;

@JsonSubTypes({
		@JsonSubTypes.Type(ExecutionFeedbackPacket.class),
		@JsonSubTypes.Type(HeartbeatPacket.class),
})
@JsonExplicit
public interface FeedbackPacket extends TypedDocumentedPacket<FeedbackPacketType>, EventPacket {
}
