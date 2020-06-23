package me.retrodaredevil.solarthing.packets.instance;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.Packet;

public interface SourcedPacket extends Packet {
	@NotNull String getSourceId();
}
