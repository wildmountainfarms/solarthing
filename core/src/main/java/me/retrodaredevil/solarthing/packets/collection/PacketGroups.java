package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.instance.InstanceFragmentIndicatorPacket;
import me.retrodaredevil.solarthing.packets.instance.InstancePacket;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePacket;
import me.retrodaredevil.solarthing.packets.instance.InstanceTargetPacket;

import java.util.*;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("Java8MapApi") // must remain compatible with Android SDK 19
public final class PacketGroups {
	private PacketGroups(){ throw new UnsupportedOperationException(); }

	public static final Comparator<Integer> DEFAULT_FRAGMENT_ID_COMPARATOR = Integer::compare;

	public static PacketGroup createPacketGroup(Collection<? extends Packet> groups, long dateMillis){
		return createPacketGroup(groups, dateMillis, Collections.emptyMap());
	}
	public static PacketGroup createPacketGroup(Collection<? extends Packet> groups, long dateMillis, Map<? extends Packet, Long> dateMillisPacketMap){
		return new ImmutablePacketGroup(groups, dateMillis, dateMillisPacketMap);
	}
	public static InstancePacketGroup createInstancePacketGroup(Collection<? extends Packet> groups, long dateMillis, String sourceId, int fragmentId){
		return createInstancePacketGroup(groups, dateMillis, sourceId, fragmentId, Collections.emptyMap());
	}
	public static InstancePacketGroup createInstancePacketGroup(Collection<? extends Packet> groups, long dateMillis, String sourceId, int fragmentId, Map<? extends Packet, Long> dateMillisPacketMap){
		return new ImmutableInstancePacketGroup(groups, dateMillis, dateMillisPacketMap, sourceId, fragmentId);
	}
	public static FragmentedPacketGroup createFragmentedPacketGroup(Collection<? extends InstancePacketGroup> instancePackets, long dateMillis) {
		return new ImmutableFragmentedPacketGroup(instancePackets, dateMillis);
	}
	public static InstancePacketGroup parseToInstancePacketGroup(PacketGroup group, DefaultInstanceOptions defaultInstanceOptions){
		if(group instanceof InstancePacketGroup){
			return (InstancePacketGroup) group;
		}
		List<Packet> packets = new ArrayList<>();
		String sourceId = defaultInstanceOptions.getDefaultSourceId();
		int fragmentId = defaultInstanceOptions.getDefaultFragmentId();
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
	public static TargetPacketGroup createTargetPacketGroup(Collection<? extends Packet> packets, long dateMillis, String sourceId, Collection<Integer> targetFragmentIds) {
		return new ImmutableTargetPacketGroup(packets, dateMillis, sourceId, targetFragmentIds);
	}
	public static TargetPacketGroup parseToTargetPacketGroup(PacketGroup packetGroup) {
		if (packetGroup instanceof TargetPacketGroup) {
			return (TargetPacketGroup) packetGroup;
		}
		List<Packet> packets = new ArrayList<>();
		InstanceSourcePacket sourcePacket = null;
		InstanceTargetPacket targetPacket = null;
		for (Packet packet : packetGroup.getPackets()) {
			if (packet instanceof InstancePacket) {
				InstancePacket instancePacket = (InstancePacket) packet;
				switch (instancePacket.getPacketType()) {
					case SOURCE: sourcePacket = (InstanceSourcePacket) packet; break;
					case TARGET: targetPacket = (InstanceTargetPacket) packet; break;
				}
			} else {
				packets.add(packet);
			}
		}
		return createTargetPacketGroup(
				packets,
				packetGroup.getDateMillis(),
				sourcePacket == null ? InstanceSourcePacket.UNUSED_SOURCE_ID : sourcePacket.getSourceId(),
				targetPacket == null ? Collections.emptyList() : targetPacket.getTargetFragmentIds()
		);
	}

	public static Map<String, List<InstancePacketGroup>> parsePackets(Collection<? extends PacketGroup> groups, DefaultInstanceOptions defaultInstanceOptions){
		Map<String, List<InstancePacketGroup>> map = new HashMap<>();
		for(PacketGroup group : groups){
			InstancePacketGroup instancePacketGroup = parseToInstancePacketGroup(group, defaultInstanceOptions);
			String sourceId = instancePacketGroup.getSourceId();
			List<InstancePacketGroup> list = map.get(sourceId);

			if(list == null){
				list = new ArrayList<>();
				map.put(sourceId, list);
			}
			list.add(instancePacketGroup);
		}
		return map;
	}
	public static Map<String, List<FragmentedPacketGroup>> sortPackets(Collection<? extends PacketGroup> groups, DefaultInstanceOptions defaultInstanceOptions, long maxTimeDistance){
		return sortPackets(groups, defaultInstanceOptions, maxTimeDistance, null);
	}
	public static Map<String, List<FragmentedPacketGroup>> sortPackets(Collection<? extends PacketGroup> groups, DefaultInstanceOptions defaultInstanceOptions, long maxTimeDistance, Long masterIdIgnoreDistance){
		Map<String, List<InstancePacketGroup>> map = parsePackets(groups, defaultInstanceOptions);
		Map<String, List<FragmentedPacketGroup>> r = new HashMap<>();
		for(Map.Entry<String, List<InstancePacketGroup>> entry : map.entrySet()) {
			r.put(entry.getKey(), sortPackets(entry.getValue(), maxTimeDistance, masterIdIgnoreDistance));
		}
		return r;
	}

	/**
	 * This method takes a list of {@link InstancePacketGroup}s and then merges them together. It does this by using the lowest fragment ID as the "master" ID and then it finds
	 * other fragments closest to it for each {@link InstancePacketGroup} with a master fragment ID. If there's a time gap for a certain master fragment ID, the next
	 * lowest fragment ID will be used for the time where the lowest is absent. This is recursive for all fragment IDs, so if there are many gaps, it will use the next lowest fragment ID.
	 * @param instancePacketGroups
	 * @param maxTimeDistance
	 * @param masterIdIgnoreDistance
	 * @return
	 */
	public static List<FragmentedPacketGroup> sortPackets(List<? extends InstancePacketGroup> instancePacketGroups, long maxTimeDistance, Long masterIdIgnoreDistance){
		Map<Integer, List<InstancePacketGroup>> fragmentMap = new HashMap<>();
		for(InstancePacketGroup packetGroup : instancePacketGroups){ // this for loop initializes fragmentMap
			final int fragmentId = packetGroup.getFragmentId();
			List<InstancePacketGroup> fragmentList = fragmentMap.get(packetGroup.getFragmentId());
			if(fragmentList == null){
				fragmentList = new ArrayList<>();
				fragmentMap.put(fragmentId, fragmentList);
			}
			fragmentList.add(packetGroup);
		}
		final List<Integer> fragmentIds;
		{ // initialize fragmentIds
			SortedSet<Integer> fragmentIdsSet = new TreeSet<>(DEFAULT_FRAGMENT_ID_COMPARATOR);
			fragmentIdsSet.addAll(fragmentMap.keySet());
			fragmentIds = new ArrayList<>(fragmentIdsSet); // now this is sorted
		}
		List<FragmentedPacketGroup> packetGroups = new ArrayList<>();
		addToPacketGroups(
				maxTimeDistance, masterIdIgnoreDistance,
				Long.MIN_VALUE, Long.MAX_VALUE,
				fragmentIds,
				fragmentMap,
				packetGroups
		);
		return packetGroups;
	}
	private static void addToPacketGroups(
			long maxTimeDistance, Long masterIdIgnoreDistance,
			long minTime, long maxTime,
			List<? extends Integer> fragmentIds,
			Map<Integer, ? extends List<? extends InstancePacketGroup>> fragmentMap,
			List<? super FragmentedPacketGroup> packetGroupsOut
	){
		if(minTime > maxTime){
			return;
		}
		int masterFragmentId = fragmentIds.get(0);
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
			subListList.add(masterList.subList(lastIndex, masterList.size()));

			List<? extends Integer> subFragmentIds = fragmentIds.subList(1, fragmentIds.size());
			InstancePacketGroup firstPacket = subListList
					.get(0) // we know this won't fail because we added at least one element above
					.get(0); // we're going to infer that this won't fail because it should never be empty
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
							lastTime + masterIdIgnoreDistance, subList.get(0).getDateMillis() - masterIdIgnoreDistance,
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
					lastSubList.get(lastSubList.size() - 1).getDateMillis() + masterIdIgnoreDistance, Long.MAX_VALUE,
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
			List<InstancePacketGroup> instancePacketGroupList = new ArrayList<>();
			instancePacketGroupList.add(masterGroup);
			for(int fragmentId : fragmentIds){
				//noinspection ConstantConditions // NOTE: This is not constant, but IntelliJ thinks it is because of the wildcard
				if(fragmentId == masterFragmentId) continue;
				List<? extends InstancePacketGroup> packetGroupList = requireNonNull(fragmentMap.get(fragmentId));
				// now we want to find the closest packet group
				InstancePacketGroup closest = getClosest(packetGroupList, masterGroup.getDateMillis());
				long smallestTime = Math.abs(closest.getDateMillis() - masterGroup.getDateMillis());
				if(smallestTime < maxTimeDistance){
					instancePacketGroupList.add(closest);
				}
			}
			packetGroupsOut.add(createFragmentedPacketGroup(instancePacketGroupList, masterGroup.getDateMillis()));
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

	public static Map<Integer, List<InstancePacketGroup>> mapFragments(Collection<? extends InstancePacketGroup> packetGroups) {
		Map<Integer, List<InstancePacketGroup>> r = new HashMap<>();
		for(InstancePacketGroup packetGroup : packetGroups) {
			int fragmentId = packetGroup.getFragmentId();
			List<InstancePacketGroup> list = r.get(fragmentId);
			if (list == null) {
				list = new ArrayList<>();
				r.put(fragmentId, list);
			}
			list.add(packetGroup);
		}
		return r;
	}
	public static List<InstancePacketGroup> orderByFragment(Collection<? extends InstancePacketGroup> instancePacketGroups) {
		return orderByFragment(instancePacketGroups, DEFAULT_FRAGMENT_ID_COMPARATOR);
	}
	public static List<InstancePacketGroup> orderByFragment(Collection<? extends InstancePacketGroup> instancePacketGroups, Comparator<Integer> fragmentComparator) {
		Map<Integer, List<InstancePacketGroup>> mappedPackets = mapFragments(instancePacketGroups);
		Collection<Integer> sortedFragmentKeys = new TreeSet<>(fragmentComparator);
		sortedFragmentKeys.addAll(mappedPackets.keySet());
		List<InstancePacketGroup> r = new ArrayList<>();
		for(int fragmentId : sortedFragmentKeys) {
			r.addAll(mappedPackets.get(fragmentId));
		}
		return r;
	}
}
