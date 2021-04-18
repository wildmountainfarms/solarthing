package me.retrodaredevil.solarthing.graphql.packets;

import me.retrodaredevil.solarthing.graphql.packets.nodes.PacketNode;

public interface PacketFilter {
	boolean keep(PacketNode<?> packetNode);

	PacketFilter KEEP_ALL = (packetNode) -> true;
}
