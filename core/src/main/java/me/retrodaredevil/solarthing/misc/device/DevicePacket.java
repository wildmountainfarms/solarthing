package me.retrodaredevil.solarthing.misc.device;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;

/**
 * A type of packet that represents device diagnostics
 */
@JsonSubTypes({
		@JsonSubTypes.Type(CpuTemperaturePacket.class)
})
public interface DevicePacket extends DocumentedPacket<DevicePacketType> {
}
