package me.retrodaredevil.solarthing.packet;

public abstract class CharSolarPacket implements SolarPacket {

	private final transient char[] chars; // transient so it's not serialized
	protected final transient String charString;

	private final PacketType packetType;

	protected int address;

	protected CharSolarPacket(char[] chars, PacketType packetType){
		this.chars = chars;
		this.charString = new String(chars);
		this.packetType = packetType;
		System.out.println(charString);
	}
//	protected abstract void init(char[] chars) throws CheckSumException, NumberFormatException;

	public static int toInt(char c) throws NumberFormatException{
//		return Integer.parseInt(c + "");
		int r = c - 48; // 0 is represented as ascii(48)
		if(r < 0 || r > 9){
			throw new NumberFormatException(c + " is not a valid decimal digit");
		}
		return r;
	}

}
