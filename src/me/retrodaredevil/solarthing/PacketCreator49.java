package me.retrodaredevil.solarthing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.retrodaredevil.solarthing.packet.fx.ImmutableFXStatusPacket;
import me.retrodaredevil.solarthing.packet.SolarPacket;
import me.retrodaredevil.solarthing.packet.mxfm.ImmutableMXFMStatusPacket;
import me.retrodaredevil.solarthing.util.CheckSumException;
import me.retrodaredevil.solarthing.util.ParsePacketAsciiDecimalDigitException;
import me.retrodaredevil.util.json.JsonFile;

public class PacketCreator49 implements PacketCreator{
	public static final char START = 10;
	public static final char END = 13;
	public static final char NULL_CHAR = 0;
	
	private final char[] bytes = new char[49];
	/** The amount of char elements initialized in the bytes array */
	private int amount = 0;

	//Public Methods
	@Override
	public Collection<SolarPacket> add(char[] chars){ // TODO set this up to tolerate (and ignore) packets that aren't 49 bytes (utilizing END char)
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
		for(char c : chars){
			bytes[amount] = c;
			amount++;
			int length = bytes.length;
			if(amount > length) {
				System.err.println("amount should never be > length because we should have reset it.");
			}
			if(amount == length){
				try{
					final SolarPacket packet = create(); // resets bytes array and amount

					System.out.println();
					System.out.println("============");
					System.out.println(JsonFile.gson.toJson(packet));
					System.out.println("============");
					System.out.println();

					if(r == null){
						r = new ArrayList<>();
					}
					r.add(packet);
				} catch(CheckSumException | UnsupportedOperationException | ParsePacketAsciiDecimalDigitException ex){
					ex.printStackTrace();
				}

			}
		}
		return r;
	}
	private SolarPacket create() throws CheckSumException, ParsePacketAsciiDecimalDigitException, UnsupportedOperationException {
		final int value = (int) bytes[1]; // ascii value
		final SolarPacket r;
		if(value >= 48 && value <= 58){ // fx status
			r = new ImmutableFXStatusPacket(bytes);
		} else if(value >= 65 && value <= 75){ // fx/fm
			r = new ImmutableMXFMStatusPacket(bytes);
		} else if(value >= 97 && value <= 106){
			throw new UnsupportedOperationException("Not set up to use FLEXnet DC Status Packets. value: " + value);
		} else {
			throw new UnsupportedOperationException("Ascii value: " + value + " not supported.");
		}

		reset();
		return r;
	}
	private void reset(){
		amount = 0;
		for(int i = 0; i < bytes.length; i++){
			bytes[i] = 0; // reset bytes array
		}
	}

}
