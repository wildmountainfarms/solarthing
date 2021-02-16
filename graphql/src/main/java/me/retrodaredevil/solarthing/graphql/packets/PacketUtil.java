package me.retrodaredevil.solarthing.graphql.packets;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class PacketUtil {
	private PacketUtil(){ throw new UnsupportedOperationException(); }

	/**
	 *
	 * @return A mutable list of packets of a certain type
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<PacketNode<T>> convertPackets(List<? extends FragmentedPacketGroup> packetGroups, Class<T> acceptClass, PacketFilter filter){
		List<PacketNode<T>> r = new ArrayList<>();
		for(FragmentedPacketGroup packetGroup : packetGroups) {
			for(Packet packet : packetGroup.getPackets()) {
				if (!acceptClass.isInstance(packet)) {
					continue;
				}
				int fragmentId = packetGroup.getFragmentId(packet);
				String sourceId = packetGroup.getSourceId(packet);
				Long dateMillis = packetGroup.getDateMillis(packet);
				if(dateMillis == null) {
					dateMillis = packetGroup.getDateMillis();
				}
				PacketNode<T> packetNode = new PacketNode<>((T) packet, dateMillis, sourceId, fragmentId);
				if(filter.keep(packetNode)) {
					r.add(packetNode);
				}
			}
		}
		return r;
	}
}
