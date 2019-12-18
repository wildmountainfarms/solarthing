package me.retrodaredevil.solarthing.packets.creation;

import me.retrodaredevil.solarthing.packets.Packet;

import java.util.*;

public class PacketListUpdaterMultiplexer implements PacketListUpdater {
	private final List<PacketListUpdater> packetProviderList;
	
	public PacketListUpdaterMultiplexer(Collection<? extends PacketListUpdater> packetProviders) {
		this.packetProviderList = Collections.unmodifiableList(new ArrayList<>(packetProviders));
	}
	public PacketListUpdaterMultiplexer(PacketListUpdater... packetProviders){
		this(Arrays.asList(packetProviders));
	}

	@Override
	public void updatePackets(List<Packet> packets) {
		for(PacketListUpdater provider : packetProviderList){
			provider.updatePackets(packets);
		}
	}

}
