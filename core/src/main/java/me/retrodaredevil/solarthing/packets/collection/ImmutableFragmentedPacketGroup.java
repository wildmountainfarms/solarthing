package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.Packet;
import org.jspecify.annotations.NullMarked;

import java.util.*;

import static java.util.Objects.requireNonNull;

@NullMarked
class ImmutableFragmentedPacketGroup extends ImmutablePacketGroup implements FragmentedPacketGroup {
	private final Map<Packet, String> sourceIdMap;
	private final Map<Packet, Integer> fragmentIdMap;

	ImmutableFragmentedPacketGroup(Collection<? extends InstancePacketGroup> instancePackets, long dateMillis) {
		super(packetHelper(instancePackets), dateMillis, dateMillisHelper(instancePackets));
		Map<Packet, Integer> fragmentIdMap = new HashMap<>();
		Map<Packet, String> sourceIdMap = new HashMap<>();

		for(InstancePacketGroup instancePacketGroup : instancePackets){
			String sourceId = instancePacketGroup.getSourceId();
			int fragmentId = instancePacketGroup.getFragmentId();
			for(Packet packet : instancePacketGroup.getPackets()){
				sourceIdMap.put(packet, sourceId);
				fragmentIdMap.put(packet, fragmentId);
			}
		}
		this.sourceIdMap = Collections.unmodifiableMap(sourceIdMap);
		this.fragmentIdMap = Collections.unmodifiableMap(fragmentIdMap);
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
			long dateMillis = instancePacketGroup.getDateMillis();
			for(Packet packet : instancePacketGroup.getPackets()){
				dateMillisPacketMap.put(packet, dateMillis);
			}
		}
		return dateMillisPacketMap;
	}

	@Override
	public String getSourceId(Packet packet) {
		return requireNonNull(sourceIdMap.get(packet));
	}

	@Override
	public int getFragmentId(Packet packet) {
		return requireNonNull(fragmentIdMap.get(packet), "provided packet is not apart of this packet group");
	}

	@Override
	public boolean hasFragmentId(int fragmentId) {
		return fragmentIdMap.containsValue(fragmentId);
	}
}
