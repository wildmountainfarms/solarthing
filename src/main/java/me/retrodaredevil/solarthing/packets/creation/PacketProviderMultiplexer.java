package me.retrodaredevil.solarthing.packets.creation;

import me.retrodaredevil.solarthing.packets.Packet;

import java.util.*;

public class PacketProviderMultiplexer implements PacketProvider {
	private final List<PacketProvider> packetProviderList;
	
	public PacketProviderMultiplexer(Collection<? extends PacketProvider> packetProviders) {
		this.packetProviderList = Collections.unmodifiableList(new ArrayList<>(packetProviders));
	}
	public PacketProviderMultiplexer(PacketProvider... packetProviders){
		this(Arrays.asList(packetProviders));
	}
	
	@Override
	public Collection<? extends Packet> createPackets() {
		List<Packet> r = null;
		for(PacketProvider provider : packetProviderList){
			Collection<? extends Packet> packets = provider.createPackets();
			if(!packets.isEmpty()){
				if(r == null){
					r = new ArrayList<>(packets);
				} else {
					r.addAll(packets);
				}
			}
		}
		return r == null ? Collections.emptySet() : r;
	}
}
