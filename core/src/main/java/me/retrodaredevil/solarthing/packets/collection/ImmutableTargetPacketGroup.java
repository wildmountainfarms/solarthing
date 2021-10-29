package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.Packet;

import java.util.*;

import static java.util.Objects.requireNonNull;

class ImmutableTargetPacketGroup implements TargetPacketGroup {

	private final List<Packet> packets;
	private final long dateMillis;
	private final String sourceId;
	private final Collection<Integer> targetFragmentIds;

	ImmutableTargetPacketGroup(Collection<? extends Packet> packets, long dateMillis, String sourceId, Collection<Integer> targetFragmentIds) {
		this.packets = Collections.unmodifiableList(new ArrayList<>(packets));
		this.dateMillis = dateMillis;
		requireNonNull(this.sourceId = sourceId);
		this.targetFragmentIds = targetFragmentIds == null ? null : new LinkedHashSet<>(targetFragmentIds);
	}

	@Override
	public List<Packet> getPackets() {
		return packets;
	}

	@Override
	public long getDateMillis() {
		return dateMillis;
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
