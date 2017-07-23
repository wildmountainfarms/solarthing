package me.retrodaredevil.solarthing.packet;

import java.util.Date;

public interface SolarPacket {

	public char getChar(int index) throws IndexOutOfBoundsException;
	/** 
	 * 
	 * @return A number 0 through 10 representing the port it's plugged into. Yes, including 10 and 0
	 */
	public int getPortNumber();
	
	/**
	 * 
	 * @return The packet type
	 */
	public PacketType getPacketType();
	
	/**
	 * 
	 * @return returns the date the packet was created
	 */
	public Date getDateCreated();
	
}
