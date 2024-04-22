package me.retrodaredevil.solarthing.rest.graphql;

import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketFinder {
	private final SimpleQueryHandler simpleQueryHandler;

	private final Map<IdentifierFragment, Identifiable> cacheMap = new HashMap<>();

	public PacketFinder(SimpleQueryHandler simpleQueryHandler) {
		this.simpleQueryHandler = simpleQueryHandler;
	}

	public synchronized @Nullable Identifiable findPacket(IdentifierFragment identifierFragment, long queryStart, long queryEnd) {
		Identifiable result = cacheMap.get(identifierFragment);
		if (result != null) {
			return result;
		}
		updateWithRange(queryStart, queryEnd);
		return cacheMap.get(identifierFragment);
	}
	public @Nullable Identifiable getCached(IdentifierFragment identifierFragment) {
		synchronized (cacheMap) {
			return cacheMap.get(identifierFragment);
		}
	}
	private void updateWithRange(long queryStart, long queryEnd) {
		List<? extends InstancePacketGroup> rawPackets = simpleQueryHandler.queryStatus(queryStart, queryEnd, null, null);
		synchronized (cacheMap) {
			for (InstancePacketGroup instancePacketGroup : rawPackets) {
				int fragmentId = instancePacketGroup.getFragmentId();
				for (Packet packet : instancePacketGroup.getPackets()) {
					if (packet instanceof Identifiable identifiable) {
						IdentifierFragment packetIdentifierFragment = IdentifierFragment.create(fragmentId, identifiable.getIdentifier());
						cacheMap.putIfAbsent(packetIdentifierFragment, identifiable);
					}
				}
			}
		}

	}
}
