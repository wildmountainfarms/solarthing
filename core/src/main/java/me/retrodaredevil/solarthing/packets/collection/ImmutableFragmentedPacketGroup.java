package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.Packet;

import java.util.*;

class ImmutableFragmentedPacketGroup extends ImmutablePacketGroup implements FragmentedPacketGroup {
	private final String sourceId;
	private final Map<Packet, Integer> fragmentIdMap;

	ImmutableFragmentedPacketGroup(Collection<? extends InstancePacketGroup> instancePackets, long dateMillis) {
		super(packetHelper(instancePackets), dateMillis, dateMillisHelper(instancePackets));
		sourceId = instancePackets.iterator().next().getSourceId();
		Map<Packet, Integer> fragmentIdMap = new HashMap<>();
		for(InstancePacketGroup instancePacketGroup : instancePackets){
			for(Packet packet : instancePacketGroup.getPackets()){
				fragmentIdMap.put(packet, instancePacketGroup.getFragmentId(packet));
			}
		}
		this.fragmentIdMap = fragmentIdMap;
	}
	private static List<Packet> packetHelper(Collection<? extends InstancePacketGroup> instancePackets){
		List<Packet> packetList = new ArrayList<>();
		for(InstancePacketGroup instancePacketGroup : instancePackets){
			packetList.addAll(instancePacketGroup.getPackets());
		}
		return packetList;
	}
	private static Map<? extends Packet, Long> dateMillisHelper(Collection<? extends InstancePacketGroup> instancePackets){
		Map<Packet, Long> dateMillisPacketMap = new HashMap<>();
		for(InstancePacketGroup instancePacketGroup : instancePackets){
			for(Packet packet : instancePacketGroup.getPackets()){
				Long dateMillis = instancePacketGroup.getDateMillis(packet);
				if(dateMillis == null){
					dateMillis = instancePacketGroup.getDateMillis();
				}
				dateMillisPacketMap.put(packet, dateMillis);
			}
		}
		return dateMillisPacketMap;
	}
	@Override
	public String getSourceId() {
		return sourceId;
	}

	@Override
	public Integer getFragmentId(Packet packet) {
		return fragmentIdMap.get(packet);
	}

}
