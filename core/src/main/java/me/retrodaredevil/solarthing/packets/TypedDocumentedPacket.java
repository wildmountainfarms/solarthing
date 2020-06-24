package me.retrodaredevil.solarthing.packets;

import me.retrodaredevil.solarthing.annotations.NotNull;

public interface TypedDocumentedPacket<T extends Enum<T> & DocumentedPacketType> extends DocumentedPacket {
	@Override
	@NotNull T getPacketType();
}
