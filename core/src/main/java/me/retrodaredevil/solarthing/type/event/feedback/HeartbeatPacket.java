package me.retrodaredevil.solarthing.type.event.feedback;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.annotation.JsonDeserialize;


@JsonDeserialize(as = ImmutableHeartbeatPacket.class)
@JsonTypeName("HEARTBEAT")
@JsonExplicit
@NullMarked
public interface HeartbeatPacket extends FeedbackPacket {
	@Override
	default FeedbackPacketType getPacketType() {
		return FeedbackPacketType.HEARTBEAT;
	}

	@JsonProperty("data")
	HeartbeatData getData();

	@JsonProperty("executionReason")
	ExecutionReason getExecutionReason();
}
