package me.retrodaredevil.solarthing.packet;

import lombok.Getter;

public class FXStatusPacket extends CharSolarPacket{

	@Getter
	private int inverterAddress =  0, inverterCurrent =  00, chargerCurrent =  00, buyCurrent =  00, inputVoltage =  000, outputVoltage =  000, 
		sellCurrent =  00, operatingMode =  00, errorMode =  000, acMode =  00; 		
	@Getter
	private float batteryVoltage =  00.0f;
	@Getter
	private int misc =  000, warningMode =  000, chksum =  000;
	
	private final PacketType packetType = PacketType.FX_STATUS;
//
	
	
	public FXStatusPacket(char[] chars) {
		super(chars);
		init(super.chars);
	}
	private void init(char[] chars){
		inverterAddress = Integer.parseInt(chars[1] + "");
		inverterCurrent = Integer.parseInt(chars[3] + "" + chars[4]);
		chargerCurrent = Integer.parseInt(chars[6] + "" + chars[7]);
		buyCurrent = Integer.parseInt(chars[9] + "" + chars[10]);
		inputVoltage = Integer.parseInt(chars[12] + "" + chars[13] + "" + chars[14]);
		outputVoltage = Integer.parseInt(chars[16] + "" + chars[17] + "" + chars[18]);
		
		sellCurrent = Integer.parseInt(chars[20] + "" + chars[21]);
		operatingMode = Integer.parseInt(chars[23] + "" + chars[24]);
		errorMode = Integer.parseInt(chars[26] + "" + chars[27] + "" +chars[28]); // high middle and low
		acMode = Integer.parseInt(chars[30] + "" + chars[31]);  // high and low
		
		batteryVoltage = Float.parseFloat(chars[33] + "" + chars[34] + "." + chars[35]);
		
		misc = Integer.parseInt(chars[37] + "" + chars[38] + "" + chars[39]);
		warningMode = Integer.parseInt(chars[41] + "" + chars[42] + "" + chars[43]);
		chksum = Integer.parseInt(chars[45] + "" + chars[46] + "" + chars[47]);
		
	}
	@Override
	public int getPortNumber() {
		return inverterAddress;
	}
	@Override
	public PacketType getPacketType() {
		return packetType;
	}

}
