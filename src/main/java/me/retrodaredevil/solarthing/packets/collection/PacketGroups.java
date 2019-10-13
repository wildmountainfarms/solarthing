package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.instance.InstanceFragmentIndicatorPacket;
import me.retrodaredevil.solarthing.packets.instance.InstancePacket;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePacket;

import java.util.*;

import static java.util.Objects.requireNonNull;

public final class PacketGroups {
	private PacketGroups(){ throw new UnsupportedOperationException(); }
	
	public static PacketGroup createPacketGroup(Collection<? extends Packet> groups, long dateMillis){
		return createPacketGroup(groups, dateMillis, Collections.emptyMap());
	}
	public static PacketGroup createPacketGroup(Collection<? extends Packet> groups, long dateMillis, Map<? extends Packet, Long> dateMillisPacketMap){
		return new ImmutablePacketGroup(groups, dateMillis, dateMillisPacketMap);
	}
	public static InstancePacketGroup createInstancePacketGroup(Collection<? extends Packet> groups, long dateMillis, String sourceId, Integer fragmentId){
		return createInstancePacketGroup(groups, dateMillis, sourceId, fragmentId, Collections.emptyMap());
	}
	public static InstancePacketGroup createInstancePacketGroup(Collection<? extends Packet> groups, long dateMillis, String sourceId, Integer fragmentId, Map<? extends Packet, Long> dateMillisPacketMap){
		return new ImmutableInstancePacketGroup(groups, dateMillis, dateMillisPacketMap, sourceId, fragmentId);
	}
	public static InstancePacketGroup parseToInstancePacketGroup(PacketGroup group){
		List<Packet> packets = new ArrayList<>();
		String sourceId = InstanceSourcePacket.DEFAULT_SOURCE_ID;
		Integer fragmentId = null;
		for(Packet packet : group.getPackets()){
			if (packet instanceof InstancePacket) {
				InstancePacket instancePacket = (InstancePacket) packet;
				switch(instancePacket.getPacketType()){
					case SOURCE: sourceId = ((InstanceSourcePacket) instancePacket).getSourceId(); break;
					case FRAGMENT_INDICATOR: fragmentId = ((InstanceFragmentIndicatorPacket) instancePacket).getFragmentId(); break;
					default: break;
				}
			} else {
				packets.add(packet);
			}
		}
		return createInstancePacketGroup(packets, group.getDateMillis(), sourceId, fragmentId);
	}
	public static Map<String, List<InstancePacketGroup>> parsePackets(Collection<? extends PacketGroup> groups){
		Map<String, List<InstancePacketGroup>> map = new HashMap<>();
		for(PacketGroup group : groups){
			InstancePacketGroup instancePacketGroup = parseToInstancePacketGroup(group);
			String sourceId = instancePacketGroup.getSourceId();
			List<InstancePacketGroup> list = map.get(sourceId);

			//noinspection Java8MapApi // This library must remain compatible with Android SDK 19
			if(list == null){
				list = new ArrayList<>();
				map.put(sourceId, list);
			}
			list.add(instancePacketGroup);
		}
		return map;
	}
	public static Map<String, List<PacketGroup>> sortPackets(Collection<? extends PacketGroup> groups, long maxTimeDistance){
		Map<String, List<InstancePacketGroup>> map = parsePackets(groups);
		Map<String, List<PacketGroup>> r = new HashMap<>();
		for(Map.Entry<String, List<InstancePacketGroup>> entry : map.entrySet()){
			String sourceId = entry.getKey(); // sourceId will be the same for everything in list
			List<InstancePacketGroup> list = entry.getValue();
			
			Map<Integer, List<InstancePacketGroup>> fragmentMap = new HashMap<>();
			for(InstancePacketGroup packetGroup : list){
				final Integer fragmentId = packetGroup.getFragmentId();
				List<InstancePacketGroup> fragmentList = fragmentMap.get(packetGroup.getFragmentId());
				if(fragmentList == null){
					fragmentList = new ArrayList<>();
					fragmentMap.put(fragmentId, fragmentList);
				}
				fragmentList.add(packetGroup);
			}
			SortedSet<Integer> fragmentIdsSet = new TreeSet<>((o1, o2) -> {
				if(o1 == null) return 1; // null is last in the set. Other values are ascending
				if(o2 == null) return -1;
				return o1 - o2;
			});
			fragmentIdsSet.addAll(fragmentMap.keySet());
			List<Integer> fragmentIds = new ArrayList<>(fragmentIdsSet); // now this is sorted
			Integer masterFragmentId = fragmentIds.get(0);
			List<InstancePacketGroup> masterList = requireNonNull(fragmentMap.get(masterFragmentId));
			List<PacketGroup> packetGroups = new ArrayList<>();
			for(InstancePacketGroup masterGroup : masterList){
				Map<Packet, Long> extraDateMillisPacketMap = new HashMap<>();
				List<Packet> packetList = new ArrayList<>(masterGroup.getPackets());
				for(Packet masterPacket : masterGroup.getPackets()){
					extraDateMillisPacketMap.put(masterPacket, masterGroup.getDateMillis());
				}
				for(Integer fragmentId : fragmentIds){
					if(Objects.equals(fragmentId, masterFragmentId)) continue;
					List<InstancePacketGroup> packetGroupList = requireNonNull(fragmentMap.get(fragmentId));
					// now we want to find the closest packet group
					// TODO This is a perfect place to use binary search
					InstancePacketGroup closest = null;
					Long smallestTime = null;
					for(InstancePacketGroup packetGroup : packetGroupList){
						Long timeDistance = Math.abs(packetGroup.getDateMillis() - masterGroup.getDateMillis());
						if(smallestTime == null || timeDistance < smallestTime){
							closest = packetGroup;
							smallestTime = timeDistance;
						}
					}
					requireNonNull(closest);
					requireNonNull(smallestTime);
					if(smallestTime < maxTimeDistance){
						packetList.addAll(closest.getPackets());
						for(Packet packet : closest.getPackets()){
							extraDateMillisPacketMap.put(packet, closest.getDateMillis());
						}
					}
				}
				packetGroups.add(createPacketGroup(packetList, masterGroup.getDateMillis(), extraDateMillisPacketMap));
			}
			r.put(sourceId, packetGroups);
		}
		return r;
	}
}
