package me.retrodaredevil.solarthing.rest.graphql.service;

import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.PacketNode;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.List;

public class ReversedPacketGetter implements PacketGetter {
	private final PacketGetter packetGetter;
	private final boolean reversed;

	ReversedPacketGetter(PacketGetter packetGetter, boolean reversed) {
		this.packetGetter = packetGetter;
		this.reversed = reversed;
	}

	@Override
	public <T> @NonNull List<@NonNull PacketNode<T>> getPackets(Class<T> clazz) {
		List<PacketNode<T>> r = packetGetter.getPackets(clazz);
		if (reversed) {
			Collections.reverse(r);
		}
		return r;
	}
}
