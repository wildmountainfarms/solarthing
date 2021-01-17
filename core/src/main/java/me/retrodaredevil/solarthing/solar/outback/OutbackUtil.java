package me.retrodaredevil.solarthing.solar.outback;

import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.SolarStatusPacketType;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import org.jetbrains.annotations.Contract;

import java.util.Collection;

public final class OutbackUtil {
	private OutbackUtil() { throw new UnsupportedOperationException(); }
	/**
	 * @param packets A collection of packets containing {@link SolarStatusPacket}s. Other types of packets will be ignored
	 * @return The {@link FXStatusPacket} from {@code packetCollection} or null if there were no FX packets in {@code packetCollection}
	 */
	@Contract(pure = true)
	public static FXStatusPacket getMasterFX(Collection<? extends Packet> packets){
		FXStatusPacket fx = null;
		for(Packet packet : packets){
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

	/**
	 * @param packetGroup The packet collection containing {@link SolarStatusPacket}s. Other types of packets will be ignored
	 * @return The {@link FXStatusPacket} from {@code packetCollection} or null if there were no FX packets in {@code packetCollection}
	 */
	@Contract(pure = true)
	public static @Nullable FXStatusPacket getMasterFX(PacketGroup packetGroup){
		return getMasterFX(packetGroup.getPackets());
	}
}
