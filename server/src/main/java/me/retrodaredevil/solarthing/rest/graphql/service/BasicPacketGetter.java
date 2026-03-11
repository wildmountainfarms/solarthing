package me.retrodaredevil.solarthing.rest.graphql.service;

import me.retrodaredevil.solarthing.rest.graphql.packets.PacketFilter;
import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.PacketNode;
import org.jspecify.annotations.NonNull;
import me.retrodaredevil.solarthing.rest.graphql.packets.PacketUtil;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class BasicPacketGetter implements PacketGetter {
	private final List<? extends FragmentedPacketGroup> packets;
	private final PacketFilter packetFilter;

	public BasicPacketGetter(List<? extends FragmentedPacketGroup> packets, PacketFilter packetFilter) {
		this.packets = requireNonNull(packets);
		this.packetFilter = requireNonNull(packetFilter);
	}
	@Override
	public <T> @NonNull List<@NonNull PacketNode<T>> getPackets(Class<T> clazz) {
		return PacketUtil.convertPackets(packets, clazz, packetFilter);
	}
}
