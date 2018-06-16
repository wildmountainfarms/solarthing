package me.retrodaredevil.solarthing.packet;

import java.util.Date;

public abstract class CharSolarPacket implements SolarPacket {

	protected final transient char[] chars; // transient so it's not serialized
	private final Date date;
	
	protected CharSolarPacket(char[] chars){
		this.chars = chars;
		date = new Date();
	}

	public static int toInt(char c){
//		return Integer.parseInt(c + "");
		int r = c - 48; // 0 is represented as ascii(48)
		if(r < 0 || r > 9){
			throw new NumberFormatException(c + " is not a valid decimal digit");
		}
		return r;
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
