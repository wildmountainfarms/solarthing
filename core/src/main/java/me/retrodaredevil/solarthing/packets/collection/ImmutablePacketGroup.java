package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.Packet;
import org.jspecify.annotations.NullMarked;

import java.util.*;

import static java.util.Objects.requireNonNull;

@NullMarked
class ImmutablePacketGroup implements PacketGroup {

	private final List<Packet> packets;
	private final long dateMillis;
	private final Map<Packet, Long> dateMillisPacketMap;

	ImmutablePacketGroup(Collection<? extends Packet> packets, long dateMillis, Map<? extends Packet, Long> dateMillisPacketMap) {
		this.packets = Collections.unmodifiableList(new ArrayList<>(packets));
		this.dateMillis = dateMillis;
		this.dateMillisPacketMap = Collections.unmodifiableMap(new HashMap<>(dateMillisPacketMap));
	}


	@Override
	public final List<Packet> getPackets() {
		return packets;
	}

	@Override
	public final long getDateMillis() {
		return dateMillis;
	}

	@Override
	public final Long getDateMillis(Packet packet) {
		return requireNonNull(dateMillisPacketMap.get(packet), "provided packet is not apart of this packet group");
	}
}
