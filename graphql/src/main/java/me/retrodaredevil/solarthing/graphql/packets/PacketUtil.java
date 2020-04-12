package me.retrodaredevil.solarthing.graphql.packets;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;

import java.util.ArrayList;
import java.util.List;

public final class PacketUtil {
	private PacketUtil(){ throw new UnsupportedOperationException(); }

	@SuppressWarnings("unchecked")
	public static <T> List<PacketNode<T>> convertPackets(List<? extends FragmentedPacketGroup> packetGroups, Class<T> acceptClass){
		List<PacketNode<T>> r = new ArrayList<>();
		for(FragmentedPacketGroup packetGroup : packetGroups) {
			String sourceId = packetGroup.getSourceId();
			for(Packet packet : packetGroup.getPackets()) {
				if (!acceptClass.isInstance(packet)) {
					continue;
				}
				Integer fragmentId = packetGroup.getFragmentId(packet);
				Long dateMillis = packetGroup.getDateMillis(packet);
				if(dateMillis == null) {
					dateMillis = packetGroup.getDateMillis();
				}
				r.add(new PacketNode<>((T) packet, dateMillis, sourceId, fragmentId));
			}
		}
		return r;
	}
}
