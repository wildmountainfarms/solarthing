package me.retrodaredevil.solarthing.commands.packets.status;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;
import org.jspecify.annotations.NullMarked;

@JsonSubTypes({
		@JsonSubTypes.Type(AvailableCommandsPacket.class)
})
@NullMarked
public interface CommandStatusPacket extends TypedDocumentedPacket<CommandStatusPacketType> {
}
