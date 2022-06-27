package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@UtilityClass
public class PacketUtil {
	private PacketUtil() { throw new UnsupportedOperationException(); }

	public static @Nullable List<FragmentedPacketGroup> getPacketGroups(String sourceId, DefaultInstanceOptions defaultInstanceOptions, List<? extends PacketGroup> packetGroups){
		// We use the SHORT* variants here because we want data from *right now*. This is especially good for when the automation program uses it,
		//   because it can quickly switch master fragments.
		Map<String, List<FragmentedPacketGroup>> packetGroupsMap = PacketGroups.sortPackets(
				packetGroups, defaultInstanceOptions,
				SolarThingConstants.SHORT_MAX_TIME_DISTANCE.toMillis(), SolarThingConstants.SHORT_MASTER_ID_IGNORE_DISTANCE.toMillis()
		);
		if(sourceId == null){ // no preference on the source
			if(packetGroupsMap.containsKey(defaultInstanceOptions.getDefaultSourceId())){
				return packetGroupsMap.get(defaultInstanceOptions.getDefaultSourceId());
			}
			Iterator<List<FragmentedPacketGroup>> iterator = packetGroupsMap.values().iterator();
			if(iterator.hasNext()){
				return iterator.next();
			}
			return null;
		}
		return packetGroupsMap.get(sourceId);
	}
}
