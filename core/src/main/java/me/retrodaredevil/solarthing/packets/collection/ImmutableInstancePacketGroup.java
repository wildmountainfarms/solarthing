package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.Packet;

import java.util.Collection;
import java.util.Map;

import static java.util.Objects.requireNonNull;

class ImmutableInstancePacketGroup extends ImmutablePacketGroup implements InstancePacketGroup {
	private final String sourceId;
	private final int fragmentId;
	ImmutableInstancePacketGroup(Collection<? extends Packet> packets, long dateMillis, Map<? extends Packet, Long> dateMillisPacketMap, String sourceId, int fragmentId) {
		super(packets, dateMillis, dateMillisPacketMap);
		requireNonNull(this.sourceId = sourceId);
		this.fragmentId = fragmentId;
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
