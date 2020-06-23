package me.retrodaredevil.solarthing.graphql.packets;

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
