package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.Packet;

import java.util.*;

import static java.util.Objects.requireNonNull;

class ImmutableInstancePacketGroup implements InstancePacketGroup {
	private final List<Packet> packets;
	private final long dateMillis;
	private final String sourceId;
	private final int fragmentId;
	ImmutableInstancePacketGroup(Collection<? extends Packet> packets, long dateMillis, String sourceId, int fragmentId) {
		this.packets = Collections.unmodifiableList(new ArrayList<>(packets));
		this.dateMillis = dateMillis;
		requireNonNull(this.sourceId = sourceId);
		this.fragmentId = fragmentId;
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
	public int getFragmentId() {
		return fragmentId;
	}
}
