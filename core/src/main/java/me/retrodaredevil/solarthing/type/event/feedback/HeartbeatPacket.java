package me.retrodaredevil.solarthing.type.event.feedback;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import org.jspecify.annotations.NonNull;


@JsonDeserialize(as = ImmutableHeartbeatPacket.class)
@JsonTypeName("HEARTBEAT")
@JsonExplicit
public interface HeartbeatPacket extends FeedbackPacket {
	@Override
	default @NonNull FeedbackPacketType getPacketType() {
		return FeedbackPacketType.HEARTBEAT;
	}

	@JsonProperty("data")
	@NonNull HeartbeatData getData();

	@JsonProperty("executionReason")
	@NonNull ExecutionReason getExecutionReason();
}
