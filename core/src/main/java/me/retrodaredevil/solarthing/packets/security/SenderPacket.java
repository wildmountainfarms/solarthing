package me.retrodaredevil.solarthing.packets.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.Packet;

public interface SenderPacket extends Packet {
	
	/**
	 * Should be serialized as "sender"
	 * @return The sender of this packet
	 */
	@JsonProperty("sender")
	String getSender();

}
