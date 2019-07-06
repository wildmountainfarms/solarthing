package me.retrodaredevil.solarthing.solar.outback;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.solar.SolarPacket;
import me.retrodaredevil.solarthing.solar.SolarPacketType;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;

public final class OutbackUtil {
	private OutbackUtil() { throw new UnsupportedOperationException(); }
	
	/**
	 *
	 * @param packetCollection The packet collection containing only {@link me.retrodaredevil.solarthing.solar.SolarPacket}s
	 * @return The {@link FXStatusPacket} from {@code packetCollection} or null if there were no FX packets in {@code packetCollection}
	 */
	public static FXStatusPacket getMasterFX(PacketCollection packetCollection){
		FXStatusPacket fx = null;
		for(Packet packet : packetCollection.getPackets()){
			SolarPacket solarPacket = (SolarPacket) packet;
			if(solarPacket.getPacketType() == SolarPacketType.FX_STATUS){
				FXStatusPacket fxStatusPacket = (FXStatusPacket) packet;
				if(fx == null || fxStatusPacket.getAddress() < fx.getAddress()){
					fx = fxStatusPacket;
				}
			}
		}
		return fx;
	}
}
