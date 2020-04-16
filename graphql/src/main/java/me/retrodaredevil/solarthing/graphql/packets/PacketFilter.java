package me.retrodaredevil.solarthing.graphql.packets;

public interface PacketFilter {
	boolean keep(PacketNode<?> packetNode);

	PacketFilter KEEP_ALL = (packetNode) -> true;
}
