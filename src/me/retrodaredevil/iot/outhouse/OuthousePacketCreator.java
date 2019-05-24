package me.retrodaredevil.iot.outhouse;

import me.retrodaredevil.iot.packets.Packet;
import me.retrodaredevil.iot.packets.PacketCreator;
import me.retrodaredevil.iot.packets.StartEndPacketCreator;
import me.retrodaredevil.util.json.JsonFile;

import java.util.Arrays;
import java.util.Collection;

public class OuthousePacketCreator extends StartEndPacketCreator {
	
	private Double lastDistance = null;
	
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
		final Double distance;
		final int temperature;
		final int humidity;
		try {
			if(split[0].equals("null")){
				distance = lastDistance;
				lastDistance = null;
			} else {
				distance = Double.parseDouble(split[0]);
				lastDistance = distance;
			}
			temperature = Integer.parseInt(split[1]);
			humidity = Integer.parseInt(split[2]);
		} catch (NumberFormatException ex){
			throw new PacketCreationException("debugBytes: " + escape(new String(bytes)), ex);
		}
		Packet occupancy = new ImmutableOccupancyPacket(distance != null && distance < 85 ? Occupancy.OCCUPIED.getValueCode() : Occupancy.VACANT.getValueCode());
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
