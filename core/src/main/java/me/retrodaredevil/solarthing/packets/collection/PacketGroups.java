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
	@SuppressWarnings("unused")
	public static Map<String, List<PacketGroup>> sortPackets(Collection<? extends PacketGroup> groups, long maxTimeDistance){
		return sortPackets(groups, maxTimeDistance, null);
	}
	public static Map<String, List<PacketGroup>> sortPackets(Collection<? extends PacketGroup> groups, long maxTimeDistance, Long masterIdIgnoreDistance){
		Map<String, List<InstancePacketGroup>> map = parsePackets(groups);
		Map<String, List<PacketGroup>> r = new HashMap<>();
		for(Map.Entry<String, List<InstancePacketGroup>> entry : map.entrySet()){
			Map<Integer, List<InstancePacketGroup>> fragmentMap = new HashMap<>();
			for(InstancePacketGroup packetGroup : entry.getValue()){ // this for loop initializes fragmentMap
				final Integer fragmentId = packetGroup.getFragmentId();
				List<InstancePacketGroup> fragmentList = fragmentMap.get(packetGroup.getFragmentId());
				if(fragmentList == null){
					fragmentList = new ArrayList<>();
					fragmentMap.put(fragmentId, fragmentList);
				}
				fragmentList.add(packetGroup);
			}
			final List<Integer> fragmentIds;
			{ // initialize fragmentIds
				SortedSet<Integer> fragmentIdsSet = new TreeSet<>((o1, o2) -> {
					if (o1 == null) return 1; // null is last in the set. Other values are ascending
					if (o2 == null) return -1;
					return o1 - o2;
				});
				fragmentIdsSet.addAll(fragmentMap.keySet());
				fragmentIds = new ArrayList<>(fragmentIdsSet); // now this is sorted
			}
			List<PacketGroup> packetGroups = new ArrayList<>();
			addToPacketGroups(
					maxTimeDistance, masterIdIgnoreDistance,
					Long.MIN_VALUE, Long.MAX_VALUE,
					fragmentIds,
					fragmentMap,
					packetGroups
			);
			r.put(entry.getKey(), packetGroups);
		}
		return r;
	}
	private static void addToPacketGroups(
			long maxTimeDistance, Long masterIdIgnoreDistance,
			long minTime, long maxTime,
			List<? extends Integer> fragmentIds,
			Map<Integer, ? extends List<? extends InstancePacketGroup>> fragmentMap,
			List<? super PacketGroup> packetGroupsOut
	){
		Integer masterFragmentId = fragmentIds.get(0);
		List<? extends InstancePacketGroup> masterList = requireNonNull(fragmentMap.get(masterFragmentId));
		if(masterIdIgnoreDistance != null && fragmentIds.size() > 1){
			InstancePacketGroup last = null;
			List<Integer> splitIndexes = new ArrayList<>();
			for(int i = 0; i < masterList.size(); i++){
				InstancePacketGroup group = masterList.get(i);
				if(last != null && Math.abs(last.getDateMillis() - group.getDateMillis()) > masterIdIgnoreDistance * 2){
					splitIndexes.add(i);
				}
				last = group;
			}
			List<List<? extends InstancePacketGroup>> subListList = new ArrayList<>();
			int lastIndex = 0;
			for(int index : splitIndexes){
				subListList.add(masterList.subList(lastIndex, index));
				lastIndex = index;
			}
			InstancePacketGroup firstPacket = subListList.get(0).get(0);
			List<? extends Integer> subFragmentIds = fragmentIds.subList(1, fragmentIds.size());
			addToPacketGroups(
					maxTimeDistance, masterIdIgnoreDistance,
					Long.MIN_VALUE, firstPacket.getDateMillis() - masterIdIgnoreDistance,
					subFragmentIds, fragmentMap, packetGroupsOut
			);
			Long lastTime = null;
			for(List<? extends InstancePacketGroup> subList : subListList){
				if(lastTime != null){
					addToPacketGroups(
							maxTimeDistance, masterIdIgnoreDistance,
							lastTime, subList.get(0).getDateMillis(),
							subFragmentIds, fragmentMap, packetGroupsOut
					);
				}
				Map<Integer, List<? extends InstancePacketGroup>> subFragmentMap = new HashMap<>(fragmentMap);
				subFragmentMap.put(masterFragmentId, subList);
				addToPacketGroups(
						maxTimeDistance, null,
						minTime, maxTime, // we don't have to limit the time more because we limited the selection with subFragmentMap
						fragmentIds, subFragmentMap, packetGroupsOut
				);
				lastTime = subList.get(subList.size() - 1).getDateMillis();
			}
			List<? extends InstancePacketGroup> lastSubList = subListList.get(subListList.size() - 1);
			addToPacketGroups(
					maxTimeDistance, masterIdIgnoreDistance,
					lastSubList.get(lastSubList.size() - 1).getDateMillis(), Long.MAX_VALUE,
					subFragmentIds, fragmentMap, packetGroupsOut
			);
			return;
		}

		for(InstancePacketGroup masterGroup : masterList){
			if(masterGroup.getDateMillis() < minTime){
				continue;
			}
			if(masterGroup.getDateMillis() > maxTime){
				break;
			}
			Map<Packet, Long> extraDateMillisPacketMap = new HashMap<>();
			List<Packet> packetList = new ArrayList<>(masterGroup.getPackets());
			for(Packet masterPacket : masterGroup.getPackets()){
				extraDateMillisPacketMap.put(masterPacket, masterGroup.getDateMillis());
			}
			for(Integer fragmentId : fragmentIds){
				if(Objects.equals(fragmentId, masterFragmentId)) continue;
				List<? extends InstancePacketGroup> packetGroupList = requireNonNull(fragmentMap.get(fragmentId));
				// now we want to find the closest packet group
				InstancePacketGroup closest = getClosest(packetGroupList, masterGroup.getDateMillis());
				long smallestTime = Math.abs(closest.getDateMillis() - masterGroup.getDateMillis());
				if(smallestTime < maxTimeDistance){
					packetList.addAll(closest.getPackets());
					for(Packet packet : closest.getPackets()){
						extraDateMillisPacketMap.put(packet, closest.getDateMillis());
					}
				}
			}
			packetGroupsOut.add(createPacketGroup(packetList, masterGroup.getDateMillis(), extraDateMillisPacketMap));
		}
	}
	private static InstancePacketGroup getClosest(List<? extends InstancePacketGroup> packetGroupList, long masterGroupDateMillis){
		// TODO This is a perfect place to use binary search
		InstancePacketGroup closest = null;
		Long smallestTime = null;
		for(InstancePacketGroup packetGroup : packetGroupList){
			long timeDistance = Math.abs(packetGroup.getDateMillis() - masterGroupDateMillis);
			if(smallestTime == null || timeDistance < smallestTime){
				closest = packetGroup;
				smallestTime = timeDistance;
			}
		}
		return requireNonNull(closest);
	}
}
