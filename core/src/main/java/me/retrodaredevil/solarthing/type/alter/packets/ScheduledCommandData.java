package me.retrodaredevil.solarthing.type.alter.packets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.util.UniqueStringRepresentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@JsonExplicit
public final class ScheduledCommandData implements UniqueStringRepresentation {
	private final long scheduledTimeMillis;
	private final String commandName;
	private final List<Integer> targetFragmentIds;

	@JsonCreator
	public ScheduledCommandData(
			@JsonProperty(value = "scheduledTimeMillis", required = true) long scheduledTimeMillis,
			@JsonProperty(value = "commandName", required = true) String commandName,
			@JsonProperty(value = "targetFragmentIds", required = true) Collection<? extends Integer> targetFragmentIds) {
		this.scheduledTimeMillis = scheduledTimeMillis;
		requireNonNull(this.commandName = commandName);
		this.targetFragmentIds = new ArrayList<>(targetFragmentIds);
	}

	@JsonProperty("scheduledTimeMillis")
	public long getScheduledTimeMillis() {
		return scheduledTimeMillis;
	}

	@JsonProperty("commandName")
	public @NotNull String getCommandName() {
		return commandName;
	}

	@JsonProperty("targetFragmentIds")
	public @NotNull Collection<Integer> getTargetFragmentIds() {
		return targetFragmentIds;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ScheduledCommandData that = (ScheduledCommandData) o;
		return scheduledTimeMillis == that.scheduledTimeMillis && commandName.equals(that.commandName) && targetFragmentIds.equals(that.targetFragmentIds);
	}

	@Override
	public int hashCode() {
		return Objects.hash(scheduledTimeMillis, commandName, targetFragmentIds);
	}

	@Override
	public @NotNull String getUniqueString() {
		return "ScheduledCommandData(" +
				"scheduledTimeMillis=" + scheduledTimeMillis +
				", commandName='" + commandName + '\'' +
				", targetFragmentIds=" + targetFragmentIds +
				')';

	}

	@Override
	public String toString() {
		return getUniqueString();
	}
}
