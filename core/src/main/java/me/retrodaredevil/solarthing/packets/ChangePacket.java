package me.retrodaredevil.solarthing.packets;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ChangePacket extends Packet {
	boolean isLastUnknown();
}
