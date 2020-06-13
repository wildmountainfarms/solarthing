package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;

@JsonSubTypes({
		@JsonSubTypes.Type(RequestCommandPacket.class)
})
public interface CommandOpenPacket extends DocumentedPacket<CommandOpenPacketType> {
}
