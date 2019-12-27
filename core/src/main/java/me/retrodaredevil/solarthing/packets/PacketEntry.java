package me.retrodaredevil.solarthing.packets;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a packet that will be directly stored in the database
 */
public interface PacketEntry extends Packet {
	/**
	 *
	 * @return The string being used to store this packet in the database
	 */
	@JsonProperty("_id")
	String getDbId();
}
