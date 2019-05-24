package me.retrodaredevil.iot.packets;

public interface DocumentedPacket<T extends Enum<T> & DocumentedPacketType> extends Packet{
	/**
	 * Should be serialized as "packetType"
	 * @return The packet type
	 */
	T getPacketType();
}
