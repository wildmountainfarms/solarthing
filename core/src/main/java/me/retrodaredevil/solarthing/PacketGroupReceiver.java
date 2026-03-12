package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.packets.collection.TargetPacketGroup;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PacketGroupReceiver {
	void receivePacketGroup(String sender, TargetPacketGroup packetGroup);
}
