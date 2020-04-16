package me.retrodaredevil.solarthing.graphql.packets;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FragmentFilter implements PacketFilter {
	private final Integer fragmentId;

	public FragmentFilter(@Nullable Integer fragmentId) {
		this.fragmentId = fragmentId;
	}

	@Override
	public boolean keep(PacketNode<?> packetNode) {
		return Objects.equals(packetNode.getFragmentId(), fragmentId);
	}
}
