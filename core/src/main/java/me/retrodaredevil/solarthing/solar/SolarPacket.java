package me.retrodaredevil.solarthing.solar;

import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;

public interface SolarPacket<T extends Enum<T> & DocumentedPacketType> extends TypedDocumentedPacket<T>, Identifiable {
}
