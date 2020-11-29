package me.retrodaredevil.solarthing.solar;

import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;

public final class PowerUtil {
	private PowerUtil() { throw new UnsupportedOperationException(); }

	public static Data getPowerData(PacketGroup packetGroup){
		Integer generatingW = null;
		Integer usingW = null;
		for(Packet packet : packetGroup.getPackets()){
			if(packet instanceof MXStatusPacket){
				if (generatingW == null) {
					generatingW = 0;
				}
				generatingW += ((MXStatusPacket) packet).getPVWattage();
			} else if(packet instanceof FXStatusPacket){
				if (usingW == null) {
					usingW = 0;
				}
				usingW += ((FXStatusPacket) packet).getPowerUsageWattage();
			} else if(packet instanceof RoverStatusPacket){
				if (generatingW == null) {
					generatingW = 0;
				}
				if (usingW == null) {
					usingW = 0;
				}
				generatingW += ((RoverStatusPacket) packet).getPVWattage().intValue();
				usingW += ((RoverStatusPacket) packet).getLoadPower();
			}
		}
		return new Data(generatingW, usingW);
	}

	public static class Data {
		private final Integer generatingWatts;
		private final Integer consumingWatts;

		public Data(Integer generatingWatts, Integer consumingWatts) {
			this.generatingWatts = generatingWatts;
			this.consumingWatts = consumingWatts;
		}

		public @Nullable Integer getGeneratingWatts() {
			return generatingWatts;
		}

		public @Nullable Integer getConsumingWatts() {
			return consumingWatts;
		}
	}
}
