package me.retrodaredevil.solarthing.packets.handling.implementations;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface StringPacketHandler {
	String getString(PacketCollection packetCollection);
}
