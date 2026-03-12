package me.retrodaredevil.solarthing.misc.device;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.marker.StatusPacket;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;
import org.jspecify.annotations.NullMarked;

/**
 * A type of packet that represents device diagnostics
 */
@JsonSubTypes({
		@JsonSubTypes.Type(CpuTemperaturePacket.class)
})
@NullMarked
public interface DevicePacket extends TypedDocumentedPacket<DevicePacketType>, StatusPacket {
}
