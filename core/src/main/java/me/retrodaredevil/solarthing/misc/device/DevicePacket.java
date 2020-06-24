package me.retrodaredevil.solarthing.misc.device;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;

/**
 * A type of packet that represents device diagnostics
 */
@JsonSubTypes({
		@JsonSubTypes.Type(CpuTemperaturePacket.class)
})
public interface DevicePacket extends TypedDocumentedPacket<DevicePacketType> {
	// TODO think about making this Identifiable. We could then have multiple implementations of packets. One implementation could internally store a fragmentId, another could use something else if necessary
}
