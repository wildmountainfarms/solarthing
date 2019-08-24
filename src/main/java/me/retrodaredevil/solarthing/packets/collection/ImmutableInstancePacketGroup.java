package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.Packet;

import java.util.Collection;
import java.util.Map;

class ImmutableInstancePacketGroup extends ImmutablePacketGroup implements InstancePacketGroup {
	private final String sourceId;
	private final Integer fragmentId;
	ImmutableInstancePacketGroup(Collection<? extends Packet> packets, long dateMillis, Map<? extends Packet, Long> dateMillisPacketMap, String sourceId, Integer fragmentId) {
		super(packets, dateMillis, dateMillisPacketMap);
		this.sourceId = sourceId;
		this.fragmentId = fragmentId;
	}
	
	@Override
	public String getSourceId() {
		return sourceId;
	}
	
	@Override
	public Integer getFragmentId() {
		return fragmentId;
	}
}
