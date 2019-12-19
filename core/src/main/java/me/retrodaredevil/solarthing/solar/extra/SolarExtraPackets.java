package me.retrodaredevil.solarthing.solar.extra;

import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.packets.UnknownPacketTypeException;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPackets;

public final class SolarExtraPackets {
	private SolarExtraPackets(){ throw new UnsupportedOperationException(); }
	/**
	 * @param jsonObject The {@link JsonObject} to create the {@link SolarExtraPacket} from
	 * @return The {@link SolarExtraPacket} created from {@code jsonObject}
	 * @throws UnknownPacketTypeException thrown if {@code jsonObject} isn't a {@link SolarExtraPacket}
	 */
	public static SolarExtraPacket createFromJson(JsonObject jsonObject) {
		final String packetName = jsonObject.getAsJsonPrimitive("packetType").getAsString();
		final SolarExtraPacketType packetType;
		try {
			packetType = SolarExtraPacketType.valueOf(packetName);
		} catch (IllegalArgumentException e) {
			throw new UnknownPacketTypeException("packet type name: " + packetName, e);
		}
		switch(packetType){
			case FX_DAILY:
				return DailyFXPackets.createFromJson(jsonObject);
			default:
				throw new UnsupportedOperationException();
		}
	}
}
