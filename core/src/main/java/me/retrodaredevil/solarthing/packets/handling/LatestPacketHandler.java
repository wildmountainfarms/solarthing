package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import org.jspecify.annotations.Nullable;

public class LatestPacketHandler implements PacketHandler {
	private volatile @Nullable PacketCollection lastCollection = null;

	public LatestPacketHandler() {
	}

	@Override
	public void handle(PacketCollection packetCollection) {
		lastCollection = packetCollection;
	}
	public @Nullable PacketCollection getLatestPacketCollection(){
		return lastCollection;
	}
}
