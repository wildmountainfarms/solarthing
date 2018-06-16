package me.retrodaredevil.solarthing.packet;

import java.util.Date;

public abstract class CharSolarPacket implements SolarPacket {

	protected final transient char[] chars; // transient so it's not serialized
	private final Date date;
	
	protected CharSolarPacket(char[] chars){
		this.chars = chars;
		date = new Date();
	}
	
	@Override
	public char getChar(int index) throws IndexOutOfBoundsException {
		return chars[index];
	}
	@Override
	public Date getDateCreated() {
		return date;
	}
	
}
