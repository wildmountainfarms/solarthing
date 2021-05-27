package me.retrodaredevil.solarthing.config.request;

import me.retrodaredevil.solarthing.actions.command.EnvironmentUpdater;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;

public final class DataRequesterResult {
	private final PacketListReceiver statusPacketListReceiver;
	private final EnvironmentUpdater environmentUpdater;

	public DataRequesterResult(PacketListReceiver statusPacketListReceiver, EnvironmentUpdater environmentUpdater) {
		this.statusPacketListReceiver = statusPacketListReceiver;
		this.environmentUpdater = environmentUpdater;
	}
	public DataRequesterResult(PacketListReceiver packetListReceiver) {
		this(packetListReceiver, EnvironmentUpdater.DO_NOTHING);
	}

	public PacketListReceiver getStatusPacketListReceiver() {
		return statusPacketListReceiver;
	}

	public EnvironmentUpdater getEnvironmentUpdater() {
		return environmentUpdater;
	}
}
