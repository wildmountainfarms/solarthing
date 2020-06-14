package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.packets.collection.TargetPacketGroup;

public interface PacketGroupReceiver {
	void receivePacketGroup(String sender, TargetPacketGroup packetGroup);
}
