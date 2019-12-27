package me.retrodaredevil.solarthing.solar;

import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.packets.UnknownPacketTypeException;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPackets;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPackets;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPackets;

@Deprecated
public final class SolarStatusPackets {
	private SolarStatusPackets(){ throw new UnsupportedOperationException(); }
	
	/**
	 * @param jsonObject The {@link JsonObject} to create the {@link SolarStatusPacket} from
	 * @return The {@link SolarStatusPacket} created from {@code jsonObject}
	 * @throws UnknownPacketTypeException thrown if {@code jsonObject} isn't a {@link SolarStatusPacket}
	 */
	@Deprecated
	public static SolarStatusPacket createFromJson(JsonObject jsonObject) {
		final String packetName = jsonObject.getAsJsonPrimitive("packetType").getAsString();
		final SolarStatusPacketType packetType;
		try {
			packetType = SolarStatusPacketType.valueOf(packetName);
		} catch(IllegalArgumentException e){
			throw new UnknownPacketTypeException("packet type name: " + packetName, e);
		}
		switch(packetType){
			case FX_STATUS:
				return FXStatusPackets.createFromJson(jsonObject);
			case MXFM_STATUS:
				return MXStatusPackets.createFromJson(jsonObject);
			case FLEXNET_DC_STATUS:
				throw new UnsupportedOperationException("FLEXNet DC Status Packets aren't supported yet.");
			case RENOGY_ROVER_STATUS:
				return RoverStatusPackets.createFromJson(jsonObject);
			default:
				throw new UnsupportedOperationException();
		}
	}
}
