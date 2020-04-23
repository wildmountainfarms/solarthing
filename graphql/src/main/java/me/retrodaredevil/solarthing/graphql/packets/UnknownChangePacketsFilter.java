package me.retrodaredevil.solarthing.graphql.packets;

import me.retrodaredevil.solarthing.packets.ChangePacket;

public class UnknownChangePacketsFilter implements PacketFilter {
	private final boolean includeUnknownChangePackets;

	public UnknownChangePacketsFilter(boolean includeUnknownChangePackets) {
		this.includeUnknownChangePackets = includeUnknownChangePackets;
	}
	public UnknownChangePacketsFilter() {
		this(false);
	}

	@Override
	public boolean keep(PacketNode<?> packetNode) {
		if (includeUnknownChangePackets) {
			return true;
		}
		Object packet = packetNode.getPacket();
		if (packet instanceof ChangePacket) {
			return !((ChangePacket) packet).isLastUnknown();
		}
		return true;
	}
}
