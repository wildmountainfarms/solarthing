package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.program.subprogram.run.PacketListReceiverHandler;

/**
 * An environment that contains objects used to put data in the event database
 */
public class EventReceiverEnvironment {
	private final PacketListReceiverHandler eventPacketListReceiverHandler;

	public EventReceiverEnvironment(PacketListReceiverHandler eventPacketListReceiverHandler) {
		this.eventPacketListReceiverHandler = eventPacketListReceiverHandler;
	}

	public PacketListReceiverHandler getEventPacketListReceiverHandler() {
		return eventPacketListReceiverHandler;
	}
}
