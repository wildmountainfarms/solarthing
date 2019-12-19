package me.retrodaredevil.solarthing.packets.creation;

import me.retrodaredevil.solarthing.packets.Packet;

import java.util.Collection;

/**
 * A packet creator is something that creates packets from characters from some sort of stream. The characters may be
 * received at different time intervals.
 */
public interface TextPacketCreator {

	/**
	 * 
	 * @param bytes the characters to read from
	 * @return An empty collection, or if it can be read, a collection of {@link Packet}s
	 */
	Collection<? extends Packet> add(char[] bytes) throws PacketCreationException;
	
}
