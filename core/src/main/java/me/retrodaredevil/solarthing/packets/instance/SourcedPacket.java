package me.retrodaredevil.solarthing.packets.instance;

import me.retrodaredevil.solarthing.packets.Packet;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SourcedPacket extends Packet {
	String getSourceId();
}
