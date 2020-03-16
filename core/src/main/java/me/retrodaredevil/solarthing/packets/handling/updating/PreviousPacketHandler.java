package me.retrodaredevil.solarthing.packets.handling.updating;

import me.retrodaredevil.solarthing.packets.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PreviousPacketHandler<T extends Packet> {
	List<? extends Packet> getPackets(T current, @Nullable T previous);
}
