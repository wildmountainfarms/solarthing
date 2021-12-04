package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.packets.collection.TargetPacketGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PacketGroupReceiverMultiplexer implements PacketGroupReceiver {
	private final List<PacketGroupReceiver> packetGroupReceivers;

	public PacketGroupReceiverMultiplexer(Collection<? extends PacketGroupReceiver> packetGroupReceivers) {
		this.packetGroupReceivers = Collections.unmodifiableList(new ArrayList<>(packetGroupReceivers));
	}

	@Override
	public void receivePacketGroup(String sender, TargetPacketGroup packetGroup) {
		for (PacketGroupReceiver receiver : packetGroupReceivers) {
			receiver.receivePacketGroup(sender, packetGroup);
		}
	}
}
