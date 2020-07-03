package me.retrodaredevil.solarthing.graphql.service;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.graphql.packets.PacketNode;

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
	public @NotNull <T> List<@NotNull PacketNode<T>> getPackets(Class<T> clazz) {
		List<PacketNode<T>> r = packetGetter.getPackets(clazz);
		if (reversed) {
			Collections.reverse(r);
		}
		return r;
	}
}
