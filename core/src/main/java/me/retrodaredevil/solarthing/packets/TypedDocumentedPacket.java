package me.retrodaredevil.solarthing.packets;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface TypedDocumentedPacket<T extends Enum<T> & DocumentedPacketType> extends DocumentedPacket {
	@Override
	T getPacketType();
}
