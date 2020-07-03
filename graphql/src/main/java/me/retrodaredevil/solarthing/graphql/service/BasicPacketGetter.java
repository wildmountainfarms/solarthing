package me.retrodaredevil.solarthing.graphql.service;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.graphql.packets.PacketFilter;
import me.retrodaredevil.solarthing.graphql.packets.PacketNode;
import me.retrodaredevil.solarthing.graphql.packets.PacketUtil;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class BasicPacketGetter implements PacketGetter {
	private final List<? extends FragmentedPacketGroup> packets;
	private final PacketFilter packetFilter;

	public BasicPacketGetter(List<? extends FragmentedPacketGroup> packets, PacketFilter packetFilter) {
		this.packets = requireNonNull(packets);
		this.packetFilter = packetFilter;
	}
	@Override
	public <T> @NotNull List<@NotNull PacketNode<T>> getPackets(Class<T> clazz) {
		return PacketUtil.convertPackets(packets, clazz, packetFilter);
	}
}
