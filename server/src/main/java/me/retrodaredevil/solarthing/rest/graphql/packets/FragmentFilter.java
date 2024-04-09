package me.retrodaredevil.solarthing.rest.graphql.packets;

import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.PacketNode;

/**
 * @deprecated Should not be needed anymore as {@link me.retrodaredevil.solarthing.rest.graphql.SimpleQueryHandler} accepts fragmentIds for most of its methods now.
 */
@Deprecated
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
