package me.retrodaredevil.solarthing.packet;

public interface StatusPacket extends SolarPacket {
	PacketType getPacketType();
	int getAddress();
}
