package me.retrodaredevil.solarthing.alter;

import me.retrodaredevil.solarthing.packets.PacketEntry;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;

/**
 * Represents a packet that is directly stored in the alter database
 */
public interface AlterPacket extends PacketEntry, TypedDocumentedPacket<AlterPacketType> {
}
