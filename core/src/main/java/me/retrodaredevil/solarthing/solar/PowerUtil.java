package me.retrodaredevil.solarthing.solar;

import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import me.retrodaredevil.solarthing.solar.tracer.TracerStatusPacket;

import static java.util.Objects.requireNonNull;

@UtilityClass
public final class PowerUtil {
	private PowerUtil() { throw new UnsupportedOperationException(); }

	public static Data getPowerData(PacketGroup packetGroup, GeneratingType generatingType){
		requireNonNull(packetGroup);
		requireNonNull(generatingType);

		Integer generatingW = null;
		Integer usingW = null;
		for(Packet packet : packetGroup.getPackets()){
			if(packet instanceof MXStatusPacket){
				if (generatingW == null) {
					generatingW = 0;
				}
				if (generatingType == GeneratingType.PV_ONLY) {
					generatingW += ((MXStatusPacket) packet).getPVWattage();
				} else if (generatingType == GeneratingType.TOTAL_CHARGING) {
					generatingW += ((MXStatusPacket) packet).getChargingPower().intValue();
				} else throw new AssertionError("Unknown generatingType: " + generatingType);
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
				if (generatingType == GeneratingType.PV_ONLY) {
					generatingW += ((RoverStatusPacket) packet).getPVWattage().intValue();
				} else if (generatingType == GeneratingType.TOTAL_CHARGING) {
					generatingW += ((RoverStatusPacket) packet).getChargingPower();
				} else throw new AssertionError("Unknown generatingType: " + generatingType);
				usingW += ((RoverStatusPacket) packet).getLoadPower();
			} else if (packet instanceof TracerStatusPacket) {
				if (generatingW == null) {
					generatingW = 0;
				}
				if (usingW == null) {
					usingW = 0;
				}
				if (generatingType == GeneratingType.PV_ONLY) {
					generatingW += ((TracerStatusPacket) packet).getPVWattage().intValue();
				} else if (generatingType == GeneratingType.TOTAL_CHARGING) {
					generatingW += ((TracerStatusPacket) packet).getChargingPower().intValue();
				} else throw new AssertionError("Unknown generatingType: " + generatingType);
				usingW += (int) ((TracerStatusPacket) packet).getLoadPower();
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
	public enum GeneratingType {
		PV_ONLY,
		TOTAL_CHARGING
	}
}
