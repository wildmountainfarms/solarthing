package me.retrodaredevil.solarthing.cache;

import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;

public interface CacheDataPacket extends TypedDocumentedPacket<CacheDataPacketType> {
	// All cached data is required to be a type from CacheDataType because there
	//   will always be a set of known CacheData types. There's never a possibility that
	//   it could be defined elsewhere like there is with some random packet in a PacketGroup
}
