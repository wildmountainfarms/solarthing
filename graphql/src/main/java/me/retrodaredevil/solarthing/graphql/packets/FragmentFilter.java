package me.retrodaredevil.solarthing.graphql.packets;

import me.retrodaredevil.solarthing.graphql.packets.nodes.PacketNode;

public class FragmentFilter implements PacketFilter {
	private final int fragmentId;

	public FragmentFilter(int fragmentId) {
		this.fragmentId = fragmentId;
	}

	@Override
	public boolean keep(PacketNode<?> packetNode) {
		return packetNode.getFragmentId() == fragmentId;
	}
}
