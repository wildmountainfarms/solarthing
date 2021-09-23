package me.retrodaredevil.solarthing.type.alter;

import me.retrodaredevil.solarthing.annotations.WorkInProgress;
import me.retrodaredevil.solarthing.packets.PacketEntry;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;

/**
 * Represents a packet that is directly stored in the alter database
 */
@WorkInProgress // eventually this will have a JsonSubType annotation on it
public interface AlterPacket extends PacketEntry, TypedDocumentedPacket<AlterPacketType> {
}
