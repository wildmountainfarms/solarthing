package me.retrodaredevil.solarthing.solar.outback;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.StartEndPacketCreator;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPackets;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPackets;
import me.retrodaredevil.solarthing.util.CheckSumException;
import me.retrodaredevil.solarthing.util.IgnoreCheckSum;
import me.retrodaredevil.solarthing.util.ParsePacketAsciiDecimalDigitException;
import me.retrodaredevil.util.json.JsonFile;

import java.util.Collection;
import java.util.Collections;

public class MatePacketCreator49 extends StartEndPacketCreator {

	private final IgnoreCheckSum ignoreCheckSum;

	public MatePacketCreator49(IgnoreCheckSum ignoreCheckSum){
		super('\n', '\r', 49, 49);
		this.ignoreCheckSum = ignoreCheckSum;
	}

	@Override
	public Collection<Packet> create(char[] bytes) throws PacketCreationException{
		final int value = (int) bytes[1]; // ascii value
		final Packet r;
		if(value >= 48 && value <= 58){ // fx status
			try {
				r = FXStatusPackets.createFromChars(bytes, ignoreCheckSum);
			} catch (ParsePacketAsciiDecimalDigitException | CheckSumException e) {
				throw new PacketCreationException(e);
			}
		} else if(value >= 65 && value <= 75){ // mx
			try {
				r = MXStatusPackets.createFromChars(bytes, ignoreCheckSum);
			} catch (ParsePacketAsciiDecimalDigitException | CheckSumException e) {
				throw new PacketCreationException(e);
			}
		} else if(value >= 97 && value <= 106){
			throw new UnsupportedOperationException("Not set up to use FLEXnet DC Status Packets. value: " + value);
		} else {
			throw new UnsupportedOperationException("Ascii value: " + value + " not supported. (from: '" + new String(bytes) + "')");
		}
		System.out.println("=====");
		System.out.println(JsonFile.gson.toJson(r));
		System.out.println("=====");
		return Collections.singleton(r);
	}

}
