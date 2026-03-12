package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.Packet;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static java.util.Objects.requireNonNull;

@NullMarked
class ImmutableTargetPacketGroup implements TargetPacketGroup {

	private final List<Packet> packets;
	private final long dateMillis;
	private final String sourceId;
	private final @Nullable Collection<Integer> targetFragmentIds;

	ImmutableTargetPacketGroup(Collection<? extends Packet> packets, long dateMillis, String sourceId, @Nullable Collection<Integer> targetFragmentIds) {
		this.packets = Collections.unmodifiableList(new ArrayList<>(packets));
		this.dateMillis = dateMillis;
		this.sourceId = requireNonNull(sourceId);
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

	// TODO remove NonNull
	@Override
	public @NonNull String getSourceId() {
		return sourceId;
	}

	@Override
	public @Nullable Collection<Integer> getTargetFragmentIds() {
		return targetFragmentIds;
	}
}
