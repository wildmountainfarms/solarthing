package me.retrodaredevil.solarthing.packets.instance;

import me.retrodaredevil.solarthing.packets.Packet;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SourcedPacket extends Packet {
	// TODO remove NonNull
	@NonNull String getSourceId();
}
