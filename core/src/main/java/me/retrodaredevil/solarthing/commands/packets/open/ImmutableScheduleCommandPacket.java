package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.type.alter.packets.ScheduledCommandData;

import java.util.Objects;
import java.util.UUID;

public class ImmutableScheduleCommandPacket implements ScheduleCommandPacket {
	private final ScheduledCommandData data;
	private final UUID uniqueRequestId;

	@JsonCreator
	public ImmutableScheduleCommandPacket(
			@JsonProperty("data") ScheduledCommandData data,
			@JsonProperty("uniqueRequestId") UUID uniqueRequestId) {
		this.data = data;
		this.uniqueRequestId = uniqueRequestId;
	}

	@Override
	public @NotNull ScheduledCommandData getData() {
		return data;
	}

	@Override
	public @NotNull UUID getUniqueRequestId() {
		return uniqueRequestId;
	}

	@Override
	public @NotNull String getUniqueString() {
		return "ScheduleCommandPacket(" +
				"data=" + data.getUniqueString() +
				", uniqueRequestId=" + uniqueRequestId +
				')';
	}

	@Override
	public String toString() {
		return getUniqueString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ImmutableScheduleCommandPacket that = (ImmutableScheduleCommandPacket) o;
		return data.equals(that.data) && uniqueRequestId.equals(that.uniqueRequestId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(data, uniqueRequestId);
	}
}
