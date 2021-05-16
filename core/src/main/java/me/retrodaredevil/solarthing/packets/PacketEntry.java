package me.retrodaredevil.solarthing.packets;

/**
 * Represents a packet that will be directly stored in the database.
 * <p>
 * You should avoid naming fields in certain ways: You should not have any fields that begin with an underscore.
 * Fields that begin with underscores in nested objects are fine.
 */
public interface PacketEntry extends Packet {
	/**
	 *
	 * @return The string being used to store this packet in the database
	 */
	String getDbId();
}
