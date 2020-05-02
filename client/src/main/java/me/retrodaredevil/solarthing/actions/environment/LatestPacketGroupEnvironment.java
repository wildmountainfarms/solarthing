package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.actions.PacketGroupProvider;

public class LatestPacketGroupEnvironment {
	private final PacketGroupProvider packetGroupProvider;

	public LatestPacketGroupEnvironment(PacketGroupProvider packetGroupProvider) {
		this.packetGroupProvider = packetGroupProvider;
	}

	public PacketGroupProvider getPacketGroupProvider() {
		return packetGroupProvider;
	}
}
