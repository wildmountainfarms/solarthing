package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.Packet;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import static java.util.Objects.requireNonNull;

class ImmutableTargetPacketGroup extends ImmutablePacketGroup implements TargetPacketGroup {

	private final String sourceId;
	private final Collection<Integer> targetFragmentIds;

	ImmutableTargetPacketGroup(Collection<? extends Packet> packets, long dateMillis, String sourceId, Collection<Integer> targetFragmentIds) {
		super(packets, dateMillis, Collections.emptyMap());
		requireNonNull(this.sourceId = sourceId);
		this.targetFragmentIds = targetFragmentIds == null ? null : new LinkedHashSet<>(targetFragmentIds);
	}

	@Override
	public @NotNull String getSourceId() {
		return sourceId;
	}

	@Override
	public @Nullable Collection<Integer> getTargetFragmentIds() {
		return targetFragmentIds;
	}
}
