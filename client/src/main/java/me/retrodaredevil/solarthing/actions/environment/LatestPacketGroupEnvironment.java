package me.retrodaredevil.action.node.environment;

import me.retrodaredevil.solarthing.PacketGroupProvider;

public class LatestPacketGroupEnvironment {
	private final PacketGroupProvider packetGroupProvider;

	public LatestPacketGroupEnvironment(PacketGroupProvider packetGroupProvider) {
		this.packetGroupProvider = packetGroupProvider;
	}

	public PacketGroupProvider getPacketGroupProvider() {
		return packetGroupProvider;
	}
}
