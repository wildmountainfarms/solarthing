package me.retrodaredevil.solarthing.type.alter.packets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.util.UniqueStringRepresentation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@JsonExplicit
@NullMarked
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
		this.commandName = requireNonNull(commandName);
		this.targetFragmentIds = new ArrayList<>(targetFragmentIds);
	}

	@JsonProperty("scheduledTimeMillis")
	public long getScheduledTimeMillis() {
		return scheduledTimeMillis;
	}

	// TODO remove NonNull
	@JsonProperty("commandName")
	public @NonNull String getCommandName() {
		return commandName;
	}

	// TODO remove NonNull
	@JsonProperty("targetFragmentIds")
	public @NonNull Collection<Integer> getTargetFragmentIds() {
		return targetFragmentIds;
	}

	@Override
	public boolean equals(@Nullable Object o) {
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
	public String getUniqueString() {
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
