package me.retrodaredevil.solarthing.outhouse;

import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.packets.UnknownPacketTypeException;

@SuppressWarnings("unused")
public final class OuthousePackets {
	private OuthousePackets(){ throw new UnsupportedOperationException(); }
	/**
	 * @param jsonObject The {@link JsonObject} to create the {@link OuthousePacket} from
	 * @return The {@link OuthousePacket} created from {@code jsonObject}
	 * @throws UnknownPacketTypeException thrown if {@code jsonObject} isn't a known {@link OuthousePacket}
	 */
	public static OuthousePacket createFromJson(JsonObject jsonObject) {
		final String packetName = jsonObject.getAsJsonPrimitive("packetType").getAsString();
		final OuthousePacketType packetType;
		try {
			packetType = OuthousePacketType.valueOf(packetName);
		} catch(IllegalArgumentException e){
			throw new UnknownPacketTypeException("packet type name: " + packetName, e);
		}
		switch(packetType){
			case OCCUPANCY:
				return OccupancyPackets.createFromJson(jsonObject);
			case WEATHER:
				return WeatherPackets.createFromJson(jsonObject);
			case DOOR:
				return DoorPackets.createFromJson(jsonObject);
			default:
				throw new UnsupportedOperationException();
		}
	}
}
