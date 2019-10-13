package me.retrodaredevil.solarthing.packets.security;

import me.retrodaredevil.solarthing.packets.Packet;

public interface SenderPacket extends Packet {
	
	/**
	 * Should be serialized as "sender"
	 * @return The sender of this packet
	 */
	String getSender();

}
