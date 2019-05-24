package me.retrodaredevil.iot.outhouse;

import me.retrodaredevil.iot.packets.Packet;
import me.retrodaredevil.iot.packets.StartEndPacketCreator;

import java.util.Arrays;
import java.util.Collection;

public class OuthousePacketCreator extends StartEndPacketCreator {
	
	public OuthousePacketCreator() {
		super('\n', '\r', 256, "\n0.0 0 0\r".length());
	}
	
	@Override
	protected Collection<Packet> create(char[] bytes) throws PacketCreationException {
		String data = new String(bytes, 1, bytes.length - 2);
		String[] split = data.split(data);
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
			throw new PacketCreationException("data: " + data, ex);
		}
		return Arrays.asList(
			new ImmutableOccupancyPacket(distance != null && distance < 30 ? Occupancy.OCCUPIED.getValueCode() : Occupancy.VACANT.getValueCode()),
			new IntegerWeatherPacket(temperature, humidity)
		);
	}
}
