package me.retrodaredevil.solarthing.rest.graphql.packets;

import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.PacketNode;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PacketFilter {
	boolean keep(PacketNode<?> packetNode);

	PacketFilter KEEP_ALL = (packetNode) -> true;
}
