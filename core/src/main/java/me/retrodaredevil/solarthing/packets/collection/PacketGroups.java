package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.instance.InstanceFragmentIndicatorPacket;
import me.retrodaredevil.solarthing.packets.instance.InstancePacket;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePacket;
import me.retrodaredevil.solarthing.packets.instance.InstanceTargetPacket;
import org.jetbrains.annotations.Contract;

import java.util.*;

import static java.util.Objects.requireNonNull;

@UtilityClass
public final class PacketGroups {
	private PacketGroups(){ throw new UnsupportedOperationException(); }

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

	/**
	 * Parses to a list of {@link InstancePacketGroup}s without organizing them based on their source ID
	 * @param groups The PacketGroup list to parse
	 * @param defaultInstanceOptions The default instance options
	 * @return A list of {@link InstancePacketGroup}s.
	 */
	@Contract(pure = true)
	public static List<InstancePacketGroup> parseToInstancePacketGroups(Collection<? extends PacketGroup> groups, DefaultInstanceOptions defaultInstanceOptions) {
		List<InstancePacketGroup> r = new ArrayList<>();
		for(PacketGroup group : groups){
			InstancePacketGroup instancePacketGroup = parseToInstancePacketGroup(group, defaultInstanceOptions);
			r.add(instancePacketGroup);
		}
		return r;
	}

	/**
	 * Organizes and parses a list of {@link PacketGroup}s into a map of parsed {@link InstancePacketGroup}s based on their source ID.
	 * @param groups The PacketGroup list to parse
	 * @param defaultInstanceOptions The default instance options
	 * @return A map of a list of {@link InstancePacketGroup}s where each key in the map is a different source ID.
	 */
	@Contract(pure = true)
	public static Map<String, List<InstancePacketGroup>> parsePackets(Collection<? extends PacketGroup> groups, DefaultInstanceOptions defaultInstanceOptions){
		Map<String, List<InstancePacketGroup>> map = new HashMap<>();
		for(PacketGroup group : groups){
			InstancePacketGroup instancePacketGroup = parseToInstancePacketGroup(group, defaultInstanceOptions);
			String sourceId = instancePacketGroup.getSourceId();
			List<InstancePacketGroup> list = map.computeIfAbsent(sourceId, k -> new ArrayList<>());

			list.add(instancePacketGroup);
		}
		return map;
	}

	@Contract(pure = true)
	public static Map<String, List<FragmentedPacketGroup>> sortPackets(Collection<? extends PacketGroup> groups, DefaultInstanceOptions defaultInstanceOptions, long maxTimeDistance, Long masterIdIgnoreDistance){
		return sortPackets(groups, defaultInstanceOptions, maxTimeDistance, masterIdIgnoreDistance, FragmentUtil.DEFAULT_FRAGMENT_ID_COMPARATOR);
	}
	@Contract(pure = true)
	public static Map<String, List<FragmentedPacketGroup>> sortPackets(Collection<? extends PacketGroup> groups, DefaultInstanceOptions defaultInstanceOptions, long maxTimeDistance, Long masterIdIgnoreDistance, Comparator<Integer> fragmentIdComparator){
		Map<String, List<InstancePacketGroup>> map = parsePackets(groups, defaultInstanceOptions);
		Map<String, List<FragmentedPacketGroup>> r = new HashMap<>();
		for(Map.Entry<String, List<InstancePacketGroup>> entry : map.entrySet()) {
			r.put(entry.getKey(), mergePackets(entry.getValue(), maxTimeDistance, masterIdIgnoreDistance, fragmentIdComparator));
		}
		return r;
	}

	public static List<FragmentedPacketGroup> mergePackets(List<? extends InstancePacketGroup> instancePacketGroups, long maxTimeDistance, Long masterIdIgnoreDistance){
		return mergePackets(instancePacketGroups, maxTimeDistance, masterIdIgnoreDistance, FragmentUtil.DEFAULT_FRAGMENT_ID_COMPARATOR);
	}
	/**
	 * This method takes a list of {@link InstancePacketGroup}s and then merges them together. It does this by using the lowest fragment ID as the "master" ID and then it finds
	 * other fragments closest to it for each {@link InstancePacketGroup} with a master fragment ID. If there's a time gap for a certain master fragment ID, the next
	 * lowest fragment ID will be used for the time where the lowest is absent. This is recursive for all fragment IDs, so if there are many gaps, it will use the next lowest fragment ID.
	 * @param instancePacketGroups The instance packet groups. Should be ordered oldest to newest
	 * @param maxTimeDistance The maximum amount of time in milliseconds between a master packet and a packet of another fragment ID
	 * @param masterIdIgnoreDistance The amount of time in milliseconds to allow no master ID packet until it falls through to the next ID, or null. If null, it's the same as being infinite.
	 * @return A list of the merged packets
	 */
	@Contract(pure = true)
	public static List<FragmentedPacketGroup> mergePackets(List<? extends InstancePacketGroup> instancePacketGroups, long maxTimeDistance, Long masterIdIgnoreDistance, Comparator<Integer> fragmentIdComparator){
		if (instancePacketGroups.isEmpty()) {
			throw new IllegalArgumentException("instancePacketGroups cannot be empty!");
		}
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
			SortedSet<Integer> fragmentIdsSet = new TreeSet<>(fragmentIdComparator);
			fragmentIdsSet.addAll(fragmentMap.keySet());
			fragmentIds = new ArrayList<>(fragmentIdsSet); // now this is sorted
		}
		TreeSet<FragmentedPacketGroup> packetGroups = new TreeSet<>(Comparator.comparingLong(PacketGroup::getDateMillis));
		addToPacketGroups(
				maxTimeDistance, masterIdIgnoreDistance,
				Long.MIN_VALUE, Long.MAX_VALUE,
				fragmentIds,
				fragmentMap,
				packetGroups
		);
		return new ArrayList<>(packetGroups);
	}

	/**
	 * A recursive function to add packets to {@code packetGroupsOut}
	 *
	 * @param packetGroupsOut The collection to put packets in. Note that packets may not be inserted in order.
	 */
	private static void addToPacketGroups(
			long maxTimeDistance, Long masterIdIgnoreDistance,
			long minTime, long maxTime,
			List<Integer> fragmentIds,
			Map<Integer, ? extends List<? extends InstancePacketGroup>> fragmentMap,
			Collection<? super FragmentedPacketGroup> packetGroupsOut
	){
		if(minTime > maxTime){
			return;
		}
		if (fragmentIds.isEmpty()) {
			throw new IllegalArgumentException("Fragment IDs is empty!");
		}
		int masterFragmentId = fragmentIds.get(0);
		List<? extends InstancePacketGroup> masterList = requireNonNull(fragmentMap.get(masterFragmentId));
		if(masterIdIgnoreDistance != null && fragmentIds.size() > 1){ // see if we need to check for gaps in the master list
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

			List<Integer> subFragmentIds = fragmentIds.subList(1, fragmentIds.size());
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

	/**
	 *
	 * @param packetGroupList The list of {@link InstancePacketGroup}. This MUST be in order from oldest to newest
	 * @param masterGroupDateMillis The dateMillis to get the packet closest to
	 * @return The packet group with a dateMillis closest to {@code masterGroupDateMillis}
	 */
	static <T extends PacketGroup> T getClosest(List<? extends T> packetGroupList, long masterGroupDateMillis){
		T closest = null;
		Long smallestTime = null;
		int low = 0;
		int high = packetGroupList.size() - 1;
		while (low <= high) {
			int mid = low + (high - low) / 2;
			T packetGroup = packetGroupList.get(mid);
			long timeDistance = Math.abs(packetGroup.getDateMillis() - masterGroupDateMillis);
			if(smallestTime == null || timeDistance < smallestTime){
				closest = packetGroup;
				smallestTime = timeDistance;
			}
			if (packetGroup.getDateMillis() < masterGroupDateMillis) { // search further
				low = mid + 1;
			} else { // search backward
				high = mid - 1;
			}
		}
		return requireNonNull(closest);
	}

	public static Map<Integer, List<InstancePacketGroup>> mapFragments(Collection<? extends InstancePacketGroup> packetGroups) {
		Map<Integer, List<InstancePacketGroup>> r = new HashMap<>();
		for(InstancePacketGroup packetGroup : packetGroups) {
			int fragmentId = packetGroup.getFragmentId();
			List<InstancePacketGroup> list = r.computeIfAbsent(fragmentId, k -> new ArrayList<>());
			list.add(packetGroup);
		}
		return r;
	}
	public static List<InstancePacketGroup> orderByFragment(Collection<? extends InstancePacketGroup> instancePacketGroups) {
		return orderByFragment(instancePacketGroups, FragmentUtil.DEFAULT_FRAGMENT_ID_COMPARATOR);
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
