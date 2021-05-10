package me.retrodaredevil.solarthing.program;

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

	public static List<FragmentedPacketGroup> getPacketGroups(String sourceId, DefaultInstanceOptions defaultInstanceOptions, List<? extends PacketGroup> packetGroups){
		Map<String, List<FragmentedPacketGroup>> packetGroupsMap = PacketGroups.sortPackets(packetGroups, defaultInstanceOptions, 2 * 60 * 1000, 2 * 60 * 1000L);
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
