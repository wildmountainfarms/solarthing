package me.retrodaredevil.solarthing.database.cache;

import me.retrodaredevil.solarthing.packets.Packet;
import org.jspecify.annotations.Nullable;

public interface PacketCache<T extends Packet> {
	@Nullable T getPacket();
}
