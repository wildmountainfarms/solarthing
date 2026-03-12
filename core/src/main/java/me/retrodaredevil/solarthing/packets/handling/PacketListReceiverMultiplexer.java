package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.packets.Packet;
import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
public class PacketListReceiverMultiplexer implements PacketListReceiver {
	private final List<PacketListReceiver> packetProviderList;

	public PacketListReceiverMultiplexer(Collection<? extends PacketListReceiver> packetProviders) {
		this.packetProviderList = Collections.unmodifiableList(new ArrayList<>(packetProviders));
	}
	public PacketListReceiverMultiplexer(PacketListReceiver... packetProviders){
		this(Arrays.asList(packetProviders));
	}

	@Override
	public void receive(List<Packet> packets) {
		for(PacketListReceiver provider : packetProviderList){
			provider.receive(packets);
		}
	}

}
