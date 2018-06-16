package me.retrodaredevil.solarthing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.retrodaredevil.solarthing.packet.FXStatusPacket;
import me.retrodaredevil.solarthing.packet.MXFMStatusPacket;
import me.retrodaredevil.solarthing.packet.PacketType;
import me.retrodaredevil.solarthing.packet.SolarPacket;
import me.retrodaredevil.solarthing.util.json.JsonFile;

public class PacketCreator49 implements PacketCreator{

	//Constants
	public static final char START = 10;
	public static final char END = 13;
	public static final char NULL_CHAR = 0;
	
	
	
	// Private Variables
	private final char[] bytes = new char[49];
	/** The amount of elements initialized in the bytes array */
	private int amount = 0;
	
	//Constructor
	public PacketCreator49(){
		
	}
	
	//Public Methods
	@Override
	public Collection<SolarPacket> add(char[] chars){
		List<SolarPacket> r = null;
		if(chars.length == 0){
			return null;
		}
		char first = chars[0];
		if(amount == 0 && first != START){
			return null; // gotta wait for the start char
			//this.bytes[amount] = START; // if for whatever reason it doesn't add the Start of Status Page byte to the start, we'll add it
			//amount++;
			//System.out.println("Just added the START char int value: " + ((int) START) + " chars[0]: " + getPrintValue(first) + " int value: " + ((int) first));
		}
		for(int i = 0; i < chars.length; i++, amount++){
			if(canCreate()){
				if(r == null){
					r = new ArrayList<>();
				}
				r.add(create()); // also resets bytes array and amount variable

			}
			char c = chars[i];
			bytes[amount] = c;
		}
		return r;
	}
	private boolean canCreate(){
		return amount == bytes.length;
	}
	private SolarPacket create(){
		//printBytes();
		
		int value = (int) bytes[1]; // ascii value
		PacketType type = null;
		if(value >= 48 && value <= 58){ // fx status
			type = PacketType.FX_STATUS;
		} else if(value >= 65 && value <= 75){ // fx/fm
			type = PacketType.MXFM_STATUS;
		} else if(value >= 97 && value <= 106){
			throw new UnsupportedOperationException("Not set up to use FLEXnet DC Status Packets.");
		} else {
			throw new IllegalStateException("Ascii value: " + value + " not supported.");
		}
		SolarPacket r = null;
		if(type == PacketType.FX_STATUS){
			r = new FXStatusPacket(bytes);
		} else if(type == PacketType.MXFM_STATUS){
			r = new MXFMStatusPacket(bytes);
//			MXFMStatusPacket p = (MXFMStatusPacket) r;
//			p.get
		}
		System.out.println(JsonFile.gson.toJson(r));
		
		reset();
		return r;
	}
	private void reset(){
		amount = 0;
		for(int i = 0; i < bytes.length; i++){
			bytes[i] = 0; // reset bytes array
		}
	}
	
	// Private Static
	
	
	
}
