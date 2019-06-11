package me.retrodaredevil.solarthing.packets;

@SuppressWarnings("unused")
public interface DocumentedPacket<T extends Enum<T> & DocumentedPacketType> extends Packet{
	/**
	 * Should be serialized as "packetType"
	 * @return The packet type
	 */
	T getPacketType();
}
