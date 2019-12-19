package me.retrodaredevil.solarthing.solar.outback;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.SolarStatusPacketType;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;

public final class OutbackUtil {
	private OutbackUtil() { throw new UnsupportedOperationException(); }
	
	/**
	 *
	 * @param packetCollection The packet collection containing {@link SolarStatusPacket}s. Other types of packets will be ignored
	 * @return The {@link FXStatusPacket} from {@code packetCollection} or null if there were no FX packets in {@code packetCollection}
	 */
	public static FXStatusPacket getMasterFX(PacketCollection packetCollection){
		FXStatusPacket fx = null;
		for(Packet packet : packetCollection.getPackets()){
			if(!(packet instanceof SolarStatusPacket)) continue;
			
			SolarStatusPacket solarStatusPacket = (SolarStatusPacket) packet;
			if(solarStatusPacket.getPacketType() == SolarStatusPacketType.FX_STATUS){
				FXStatusPacket fxStatusPacket = (FXStatusPacket) packet;
				if(fx == null || fxStatusPacket.getAddress() < fx.getAddress()){
					fx = fxStatusPacket;
				}
			}
		}
		return fx;
	}
}
