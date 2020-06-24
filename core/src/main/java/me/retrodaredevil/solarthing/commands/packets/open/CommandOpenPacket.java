package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;

@JsonSubTypes({
		@JsonSubTypes.Type(RequestCommandPacket.class)
})
public interface CommandOpenPacket extends TypedDocumentedPacket<CommandOpenPacketType> {
}
