package me.retrodaredevil.solarthing.outhouse;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.creation.PacketCreationException;
import me.retrodaredevil.solarthing.packets.creation.TextPacketCreator;
import me.retrodaredevil.solarthing.packets.creation.StartEndTextPacketCreator;
import me.retrodaredevil.util.json.JsonFile;

import java.util.Collection;
import java.util.Collections;

public class OuthousePacketCreator extends StartEndTextPacketCreator {
	
	public OuthousePacketCreator() {
		super('\n', '\r', 256, 0);
	}
	private static String escape(String s){
		return "'" + s.replace("\n", "\\n").replace("\r", "\\r") + "'";
	}
	
	@Override
	protected Collection<Packet> create(char[] bytes) throws PacketCreationException {
		String data = new String(bytes, 1, bytes.length - 2);
		String[] split = data.split(" ");
		
		
		if(split.length < 2){
			throw new PacketCreationException("split.length should be at least 2! It's: " + split.length);
		}
		String type = split[0];
		switch(type){
			case "OCCUPANCY":
				if(split.length != 2){
					throw new PacketCreationException("split.length should be 2 for an OCCUPANCY packet! It's: " + split.length);
				}
				final boolean occupied = Boolean.parseBoolean(split[1]);
				Occupancy currentOccupancy = occupied ? Occupancy.OCCUPIED : Occupancy.VACANT;
				Packet occupancy = new ImmutableOccupancyPacket(currentOccupancy.getValueCode());
				System.out.println(JsonFile.gson.toJson(occupancy));
				
				return Collections.singleton(occupancy);
			case "WEATHER":
				if(split.length != 3){
					throw new PacketCreationException("split.length should be 3 for an OCCUPANCY packet! It's: " + split.length);
				}
				final int temperature;
				final int humidity;
				try {
					temperature = Integer.parseInt(split[1]);
					humidity = Integer.parseInt(split[2]);
				} catch (NumberFormatException ex){
					throw new PacketCreationException("debugBytes: " + escape(new String(bytes)), ex);
				}
				Packet weather = new IntegerWeatherPacket(temperature, humidity);
				
				System.out.println(JsonFile.gson.toJson(weather));
				return Collections.singleton(weather);
			case "DOOR":
				final boolean isOpen = Boolean.parseBoolean(split[1]);
				Long lastClose = null;
				Long lastOpen = null;
				try {
					lastClose = Long.parseLong(split[2]);
				} catch(NumberFormatException ex){
				}
				try {
					lastOpen = Long.parseLong(split[3]);
				} catch(NumberFormatException ex){
				}
				Packet door = new ImmutableDoorPacket(isOpen, lastClose, lastOpen);
				System.out.println(JsonFile.gson.toJson(door));
				return Collections.singleton(door);
			default:
				throw new UnsupportedOperationException("unknown type: " + type);
		}
	}
	public static void main(String[] args) throws Exception{
		TextPacketCreator packetCreator = new OuthousePacketCreator();
		System.out.println(packetCreator.add("\n22.9 24 43\r".toCharArray()));
	}
}
