package me.retrodaredevil.solarthing.graphql.packets;

import me.retrodaredevil.solarthing.graphql.packets.nodes.PacketNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PacketFilterMultiplexer implements PacketFilter {
	private final List<PacketFilter> packetFilterList;

	public PacketFilterMultiplexer(Collection<? extends PacketFilter> packetFilterList) {
		this.packetFilterList = Collections.unmodifiableList(new ArrayList<>(packetFilterList));
	}

	@Override
	public boolean keep(PacketNode<?> packetNode) {
		for (PacketFilter filter : packetFilterList) {
			if (!filter.keep(packetNode)) {
				return false;
			}
		}
		return true;
	}
}
