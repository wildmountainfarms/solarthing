package me.retrodaredevil.solarthing.database.cache;

import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.Packet;

public interface PacketCache<T extends Packet> {
	@Nullable T getPacket();
}
