package me.retrodaredevil.solarthing.misc.device;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.marker.StatusPacket;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;

/**
 * A type of packet that represents device diagnostics
 */
@JsonSubTypes({
		@JsonSubTypes.Type(CpuTemperaturePacket.class)
})
public interface DevicePacket extends TypedDocumentedPacket<DevicePacketType>, StatusPacket {
}
