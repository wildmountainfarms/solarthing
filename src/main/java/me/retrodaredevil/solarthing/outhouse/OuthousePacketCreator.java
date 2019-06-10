package me.retrodaredevil.solarthing.outhouse;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.PacketCreator;
import me.retrodaredevil.solarthing.packets.StartEndPacketCreator;
import me.retrodaredevil.util.json.JsonFile;

import java.util.Arrays;
import java.util.Collection;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class OuthousePacketCreator extends StartEndPacketCreator {
	
	public OuthousePacketCreator() {
		super('\n', '\r', 256, "\n0.0 0 0\r".length());
	}
	private static String escape(String s){
		return "'" + s.replace("\n", "\\n").replace("\r", "\\r") + "'";
	}
	
	@Override
	protected Collection<Packet> create(char[] bytes) throws PacketCreationException {
		String data = new String(bytes, 1, bytes.length - 2);
		String[] split = data.split(" ");
		if(split.length != 3){
			throw new PacketCreationException("split.length should be 3! It's: " + split.length);
		}
		final boolean occupied;
		final int temperature;
		final int humidity;
		occupied = Boolean.parseBoolean(split[0]);
		try {
			temperature = Integer.parseInt(split[1]);
			humidity = Integer.parseInt(split[2]);
		} catch (NumberFormatException ex){
			throw new PacketCreationException("debugBytes: " + escape(new String(bytes)), ex);
		}
		Occupancy currentOccupancy = occupied ? Occupancy.OCCUPIED : Occupancy.VACANT;
		Packet occupancy = new ImmutableOccupancyPacket(currentOccupancy.getValueCode());
		Packet weather = new IntegerWeatherPacket(temperature, humidity);
		System.out.println("=====");
		System.out.println(JsonFile.gson.toJson(occupancy));
		System.out.println(JsonFile.gson.toJson(weather));
		System.out.println("=====");
		return Arrays.asList(occupancy, weather);
	}
	public static void main(String[] args){
		PacketCreator packetCreator = new OuthousePacketCreator();
		System.out.println(packetCreator.add("\n22.9 24 43\r".toCharArray()));
	}
}
