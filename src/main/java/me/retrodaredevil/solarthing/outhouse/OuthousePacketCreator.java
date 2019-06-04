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
	
	private Occupancy currentOccupancy = Occupancy.VACANT;
	private int counter = 0;
	
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
				distance = null;
			} else {
				distance = Double.parseDouble(split[0]);
			}
			temperature = Integer.parseInt(split[1]);
			humidity = Integer.parseInt(split[2]);
		} catch (NumberFormatException ex){
			throw new PacketCreationException("debugBytes: " + escape(new String(bytes)), ex);
		}
		if(distance != null){
			if(distance < 30){
				counter = 6;
			} else if(distance < 85){
				counter += 2;
			} else if(distance < 100){
				counter++;
			} else if(distance > 130){ // don't do anything between 100 and 130
				if(distance > 150) {
					counter -= 2;
				} else {
					counter--;
				}
			}
		} else {
			counter--;
		}
		counter = max(0, min(6, counter));
		if(currentOccupancy == Occupancy.VACANT){
			if(counter >= 4){
				currentOccupancy = Occupancy.OCCUPIED;
			}
		} else if(currentOccupancy == Occupancy.OCCUPIED){
			if(counter <= 1){
				currentOccupancy = Occupancy.VACANT;
			}
		} else throw new UnsupportedOperationException("unknown: " + currentOccupancy);
		System.out.println("distance was: " + distance + " counter is now: " + counter);
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
