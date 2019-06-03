package me.retrodaredevil.solarthing.packets;

import java.util.Collection;

/**
 * A packet creator is something that creates packets from characters from some sort of stream. The characters may be
 * received at different time intervals.
 */
public interface PacketCreator {

	/**
	 * 
	 * @param bytes the characters to read from
	 * @return An empty collection, or if it can be read, a collection of SolarPackets
	 */
	Collection<Packet> add(char[] bytes);
	
}
