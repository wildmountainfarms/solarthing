package me.retrodaredevil.solarthing.packets.instance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;

final class ImmutableInstanceTargetPacket implements InstanceTargetPacket {
	private final Collection<Integer> targetFragmentIds;

	@JsonCreator
	ImmutableInstanceTargetPacket(@JsonProperty(value = "targets", required = true) Collection<Integer> targetFragmentIds) {
		this.targetFragmentIds = targetFragmentIds == null ? null : new LinkedHashSet<>(targetFragmentIds);
	}

	@JsonProperty("targets")
	@Override
	public @Nullable Collection<Integer> getTargetFragmentIds() {
		return targetFragmentIds;
	}
}
