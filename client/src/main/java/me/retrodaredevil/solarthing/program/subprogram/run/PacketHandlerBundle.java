package me.retrodaredevil.solarthing.program.subprogram.run;

import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public final class PacketHandlerBundle {
	private final List<PacketHandler> statusPacketHandlers;
	private final List<PacketHandler> eventPacketHandlers;

	public PacketHandlerBundle(List<PacketHandler> statusPacketHandlers, List<PacketHandler> eventPacketHandlers) {
		this.statusPacketHandlers = statusPacketHandlers;
		this.eventPacketHandlers = eventPacketHandlers;
	}

	public List<PacketHandler> getStatusPacketHandlers() {
		return statusPacketHandlers;
	}

	public List<PacketHandler> getEventPacketHandlers() {
		return eventPacketHandlers;
	}
}
