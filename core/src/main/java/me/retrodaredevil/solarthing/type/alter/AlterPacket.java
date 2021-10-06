package me.retrodaredevil.solarthing.type.alter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;
import me.retrodaredevil.solarthing.type.alter.packets.FlagPacket;
import me.retrodaredevil.solarthing.type.alter.packets.ScheduledCommandPacket;

/**
 * Represents a packet that is directly stored in the alter database
 */
@JsonSubTypes({
		@JsonSubTypes.Type(ScheduledCommandPacket.class),
		@JsonSubTypes.Type(FlagPacket.class),
})
public interface AlterPacket extends TypedDocumentedPacket<AlterPacketType> {
}
