package me.retrodaredevil.solarthing.rest.graphql.service;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.rest.graphql.packets.PacketFilter;
import me.retrodaredevil.solarthing.rest.graphql.packets.PacketUtil;
import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.PacketNode;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class LastPacketGetter implements PacketGetter {
	private final List<? extends InstancePacketGroup> packets;
	private final PacketFilter packetFilter;

	public LastPacketGetter(List<? extends InstancePacketGroup> packets, PacketFilter packetFilter) {
		requireNonNull(this.packets = packets);
		requireNonNull(this.packetFilter = packetFilter);
	}
	@Override
	public <T> @NotNull List<@NotNull PacketNode<T>> getPackets(Class<T> clazz) {
		List<InstancePacketGroup> lastPackets = new ArrayList<>();
		for(List<InstancePacketGroup> packetGroups : PacketGroups.mapFragments(packets).values()) {
			packetGroups.stream()
					.filter(packetGroup -> packetGroup.getPackets().stream().anyMatch(clazz::isInstance))
					.reduce((first, second) -> second) // get the last element of the stream
					.ifPresent(lastPackets::add);
		}
		return PacketUtil.convertPackets(lastPackets, clazz, packetFilter);
	}
}
