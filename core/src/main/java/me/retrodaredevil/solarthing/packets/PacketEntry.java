package me.retrodaredevil.solarthing.packets;

/**
 * Represents a packet that will be directly stored in the database
 */
public interface PacketEntry extends Packet {
	/**
	 *
	 * @return The string being used to store this packet in the database
	 */
	String getDbId();
}
