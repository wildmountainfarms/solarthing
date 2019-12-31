package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.packets.handling.PacketHandler;

import java.util.List;

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
