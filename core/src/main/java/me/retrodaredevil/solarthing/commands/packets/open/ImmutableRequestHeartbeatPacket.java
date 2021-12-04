package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.type.event.feedback.HeartbeatData;

import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class ImmutableRequestHeartbeatPacket implements RequestHeartbeatPacket {
	private final HeartbeatData heartbeatData;
	private final UUID uniqueRequestId;

	@JsonCreator
	public ImmutableRequestHeartbeatPacket(
			@JsonProperty(value = "data", required = true) HeartbeatData heartbeatData,
			@JsonProperty(value = "uniqueRequestId", required = true) UUID uniqueRequestId) {
		requireNonNull(this.heartbeatData = heartbeatData);
		requireNonNull(this.uniqueRequestId = uniqueRequestId);
	}

	@Override
	public @NotNull HeartbeatData getData() {
		return heartbeatData;
	}

	@Override
	public @NotNull UUID getUniqueRequestId() {
		return uniqueRequestId;
	}

	@Override
	public @NotNull String getUniqueString() {
		return "RequestHeartbeatPacket(data=" + heartbeatData.getUniqueString() + ", uniqueRequestId=" + uniqueRequestId + ")";
	}

	@Override
	public String toString() {
		return getUniqueString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ImmutableRequestHeartbeatPacket that = (ImmutableRequestHeartbeatPacket) o;
		return heartbeatData.equals(that.heartbeatData) && uniqueRequestId.equals(that.uniqueRequestId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(heartbeatData, uniqueRequestId);
	}
}
