package me.retrodaredevil.solarthing.packets.instance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;

@NullMarked
final class ImmutableInstanceTargetPacket implements InstanceTargetPacket {
	private final @Nullable Collection<Integer> targetFragmentIds;

	@JsonCreator
	ImmutableInstanceTargetPacket(@JsonProperty(value = "targets", required = true) @Nullable Collection<Integer> targetFragmentIds) {
		this.targetFragmentIds = targetFragmentIds == null ? null : new LinkedHashSet<>(targetFragmentIds);
	}

	@JsonProperty("targets")
	@Override
	public @Nullable Collection<Integer> getTargetFragmentIds() {
		return targetFragmentIds;
	}
}
