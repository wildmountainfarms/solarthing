package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;

/**
 * Represents a command packet stored in the {@link me.retrodaredevil.solarthing.SolarThingConstants#OPEN_DATABASE}, but not usually directly in a packet collection.
 * Usually encoded or encrypted for integrity.
 */
@JsonSubTypes({
		@JsonSubTypes.Type(RequestCommandPacket.class)
})
public interface CommandOpenPacket extends TypedDocumentedPacket<CommandOpenPacketType> {
}
