package me.retrodaredevil.solarthing.packet;

import java.util.Date;

public interface SolarPacket {

	char getChar(int index) throws IndexOutOfBoundsException;
	/** 
	 * 
	 * @return A number 0 through 10 representing the port it's plugged into. Yes, including 10 and 0
	 */
	int getPortNumber();
	
	/**
	 * 
	 * @return The packet type
	 */
	PacketType getPacketType();
	
//	/**
//	 *
//	 * @return returns the date the packet was created
//	 */
//	Date getDateCreated();
	
}
