package me.retrodaredevil.solarthing.solar;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import me.retrodaredevil.solarthing.solar.tracer.TracerStatusPacket;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@UtilityClass
@NullMarked
public final class PowerUtil {
	private PowerUtil() { throw new UnsupportedOperationException(); }

	public static Data getPowerData(PacketGroup packetGroup, GeneratingType generatingType){
		requireNonNull(packetGroup);
		requireNonNull(generatingType);

		Integer generatingW = null;
		Integer usingW = null;
		for(Packet packet : packetGroup.getPackets()){
			if(packet instanceof MXStatusPacket mx){
				if (generatingW == null) {
					generatingW = 0;
				}
				if (generatingType == GeneratingType.PV_ONLY) {
					generatingW += mx.getPVWattage();
				} else if (generatingType == GeneratingType.TOTAL_CHARGING) {
					generatingW += mx.getChargingPower().intValue();
				} else throw new AssertionError("Unknown generatingType: " + generatingType);
			} else if(packet instanceof FXStatusPacket fx){
				if (usingW == null) {
					usingW = 0;
				}
				usingW += fx.getPowerUsageWattage();
			} else if(packet instanceof RoverStatusPacket rover){
				if (generatingW == null) {
					generatingW = 0;
				}
				if (usingW == null) {
					usingW = 0;
				}
				if (generatingType == GeneratingType.PV_ONLY) {
					generatingW += rover.getPVWattage().intValue();
				} else if (generatingType == GeneratingType.TOTAL_CHARGING) {
					generatingW += rover.getChargingPower();
				} else throw new AssertionError("Unknown generatingType: " + generatingType);
				usingW += rover.getLoadPower();
			} else if (packet instanceof TracerStatusPacket tracer) {
				if (generatingW == null) {
					generatingW = 0;
				}
				if (usingW == null) {
					usingW = 0;
				}
				if (generatingType == GeneratingType.PV_ONLY) {
					generatingW += tracer.getPVWattage().intValue();
				} else if (generatingType == GeneratingType.TOTAL_CHARGING) {
					generatingW += tracer.getChargingPower().intValue();
				} else throw new AssertionError("Unknown generatingType: " + generatingType);
				usingW += (int) tracer.getLoadPower();
			}
		}
		return new Data(generatingW, usingW);
	}

	public static class Data {
		private final @Nullable Integer generatingWatts;
		private final @Nullable Integer consumingWatts;

		public Data(@Nullable Integer generatingWatts, @Nullable Integer consumingWatts) {
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
