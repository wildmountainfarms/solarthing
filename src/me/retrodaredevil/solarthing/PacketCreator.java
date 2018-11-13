package me.retrodaredevil.solarthing;

import java.util.Collection;

import me.retrodaredevil.solarthing.packet.SolarPacket;
import me.retrodaredevil.solarthing.util.CheckSumException;

public interface PacketCreator {

	/**
	 * 
	 * @param bytes the characters to read from
	 * @return An empty collection, or if it can be read, a collection of SolarPackets
	 */
	Collection<SolarPacket> add(char[] bytes);
	
	
	
	
}
