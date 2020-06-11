package me.retrodaredevil.solarthing.commands.packets;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;

@JsonSubTypes({
		@JsonSubTypes.Type(AvailableCommandsPacket.class)
})
public interface CommandStatusPacket extends DocumentedPacket<CommandStatusPacketType> {
}
