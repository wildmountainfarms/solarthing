package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.type.open.OpenSourcePacket;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;

/**
 * Represents a command packet stored in the {@link me.retrodaredevil.solarthing.SolarThingConstants#OPEN_DATABASE}, but not usually directly in a packet collection.
 * Usually encoded or encrypted for integrity.
 */
@JsonSubTypes({
		@JsonSubTypes.Type(RequestCommandPacket.class),
		@JsonSubTypes.Type(ScheduleCommandPacket.class),
		@JsonSubTypes.Type(RequestFlagPacket.class),
		@JsonSubTypes.Type(DeleteAlterPacket.class),
		@JsonSubTypes.Type(RequestHeartbeatPacket.class),
		@JsonSubTypes.Type(FlagAliasAddPacket.class),
})
public interface CommandOpenPacket extends TypedDocumentedPacket<CommandOpenPacketType>, OpenSourcePacket {
}
