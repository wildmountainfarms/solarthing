package me.retrodaredevil.solarthing.solar.supplementary;

import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.packets.UnknownPacketTypeException;
import me.retrodaredevil.solarthing.solar.outback.fx.supplementary.DailyFXPackets;

public final class SupplementarySolarPackets {
	private SupplementarySolarPackets(){ throw new UnsupportedOperationException(); }
	/**
	 * @param jsonObject The {@link JsonObject} to create the {@link SupplementarySolarPacket} from
	 * @return The {@link SupplementarySolarPacket} created from {@code jsonObject}
	 * @throws UnknownPacketTypeException thrown if {@code jsonObject} isn't a {@link SupplementarySolarPacket}
	 */
	public static SupplementarySolarPacket createFromJson(JsonObject jsonObject) {
		final String packetName = jsonObject.getAsJsonPrimitive("packetType").getAsString();
		final SupplementarySolarPacketType packetType;
		try {
			packetType = SupplementarySolarPacketType.valueOf(packetName);
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
