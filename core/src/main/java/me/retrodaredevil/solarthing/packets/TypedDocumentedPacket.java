package me.retrodaredevil.solarthing.packets;

import org.jspecify.annotations.NonNull;

public interface TypedDocumentedPacket<T extends Enum<T> & DocumentedPacketType> extends DocumentedPacket {
	@Override
	@NonNull T getPacketType();
}
