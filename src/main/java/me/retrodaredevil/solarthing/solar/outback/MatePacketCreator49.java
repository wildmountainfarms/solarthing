package me.retrodaredevil.solarthing.solar.outback;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.creation.PacketCreationException;
import me.retrodaredevil.solarthing.packets.creation.StartEndTextPacketCreator;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPackets;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPackets;
import me.retrodaredevil.solarthing.util.CheckSumException;
import me.retrodaredevil.solarthing.util.IgnoreCheckSum;
import me.retrodaredevil.solarthing.util.ParsePacketAsciiDecimalDigitException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Collections;

public class MatePacketCreator49 extends StartEndTextPacketCreator {
	private static final Logger LOGGER = LogManager.getLogger(MatePacketCreator49.class);
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

	private final IgnoreCheckSum ignoreCheckSum;

	public MatePacketCreator49(IgnoreCheckSum ignoreCheckSum){
		super('\n', '\r', 49, 49);
		this.ignoreCheckSum = ignoreCheckSum;
	}

	@Override
	public Collection<Packet> create(char[] bytes) throws PacketCreationException {
		final int value = bytes[1]; // ascii value
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
			throw new PacketCreationException("Not set up to use FLEXnet DC Status Packets. value: " + value);
		} else {
			throw new PacketCreationException("Ascii value: " + value + " not supported. (from: '" + new String(bytes) + "')");
		}
		LOGGER.debug(GSON.toJson(r));
		return Collections.singleton(r);
	}

}
