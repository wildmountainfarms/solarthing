package me.retrodaredevil.solarthing;

import java.util.Collection;

import me.retrodaredevil.solarthing.packet.SolarPacket;

public interface PacketCreator {

	/**
	 * 
	 * @param bytes the characters to read from
	 * @return usually null, or if it can be read, will return a collection of SolarPackets
	 */
	public Collection<SolarPacket> add(char[] bytes);
	
	
	
	
}
