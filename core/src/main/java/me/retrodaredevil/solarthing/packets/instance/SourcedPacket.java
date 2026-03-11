package me.retrodaredevil.solarthing.packets.instance;

import me.retrodaredevil.solarthing.packets.Packet;
import org.jspecify.annotations.NonNull;

public interface SourcedPacket extends Packet {
	@NonNull String getSourceId();
}
