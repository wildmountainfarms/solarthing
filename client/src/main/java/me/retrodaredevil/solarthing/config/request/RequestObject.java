package me.retrodaredevil.solarthing.config.request;

import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import org.jspecify.annotations.NullMarked;

/**
 * Simple class to group data that is needed by {@link DataRequester}s. Note this is mutable.
 */
@NullMarked
public class RequestObject {
	private final PacketListReceiver eventPacketReceiver;

	public RequestObject(PacketListReceiver eventPacketReceiver) {
		this.eventPacketReceiver = eventPacketReceiver;
	}

	public PacketListReceiver getEventPacketReceiver() {
		return eventPacketReceiver;
	}
}
