package me.retrodaredevil.solarthing.packets.handling.updating;

import me.retrodaredevil.solarthing.packets.Packet;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public interface PreviousPacketHandler<T extends Packet> {
	List<? extends Packet> getPackets(T current, @Nullable T previous);
}
