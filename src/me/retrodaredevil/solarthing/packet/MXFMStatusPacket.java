package me.retrodaredevil.solarthing.packet;

import lombok.Getter;
import me.retrodaredevil.solarthing.PacketCreator49;

public class MXFMStatusPacket extends CharSolarPacket {

	@Getter
	private int address = 0, chargerCurrent = 00, pvCurrent = 00, inputVoltage = 000;
	@Getter
	private float dailyKWH = 00.0f, ampChargerCurrent = 0.0f;
	@Getter
	private int auxMode = 00, errorMode = 000, chargerMode = 00;
	@Getter
	private float batteryVoltage = 00.0f;
	@Getter
	private int dailyAH = 0000, chksum = 000;
	
	private final PacketType packetType = PacketType.MXFM_STATUS;
	
	public MXFMStatusPacket(char[] chars) {
		super(chars);
		try{
			init(super.chars);
		} catch(Throwable t){
			printBytes();
			
			throw t;
		}
	}
	private void init(char[] chars){
		address = ((int) chars[1]) - 65; // if it is "A" then address would be 0
		chargerCurrent = Integer.parseInt(chars[6] + "" + chars[7]);
		pvCurrent = Integer.parseInt(chars[9] + "" + chars[10]);
		inputVoltage = Integer.parseInt(chars[13] + "" + chars[14]);
		
		dailyKWH = Float.parseFloat(chars[16] + "" + chars[17] + "." + chars[18]);
		ampChargerCurrent = Float.parseFloat("0." + chars[21]);
		
		auxMode = Integer.parseInt(chars[23] + "" + chars[24]);
		errorMode = Integer.parseInt(chars[26] + "" + chars[27] + "" + chars[28]);
		chargerMode = Integer.parseInt(chars[30] + "" + chars[31]);
		
		batteryVoltage = Float.parseFloat(chars[33] + "" + chars[34] + "." + chars[35]);
		
		dailyAH = Integer.parseInt(chars[37] + "" + chars[38] + "" + chars[39] /*+ "" + chars[40]*/);// if firmware is updated, uncomment
		chksum = Integer.parseInt(chars[45] + "" + chars[46] + "" + chars[47]);
		
	}

	private void printBytes(){
		for(char c : super.chars){
			System.out.print(getPrintValue(c));
		}
		System.out.println();
		
	}
	private static String getPrintValue(char c){
		if(c == PacketCreator49.START){
			return "start";
		} else if(c == PacketCreator49.END){
			return "end";
		} else if(c == PacketCreator49.NULL_CHAR){
			return "NUL";
		}
		return c + "";
	}
	@Override
	public int getPortNumber() {
		return address;
	}

	@Override
	public PacketType getPacketType() {
		return packetType;
	}

}
