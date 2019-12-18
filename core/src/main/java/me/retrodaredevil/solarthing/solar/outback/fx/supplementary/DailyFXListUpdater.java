package me.retrodaredevil.solarthing.solar.outback.fx.supplementary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.creation.PacketListUpdater;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.solar.SolarPacketType;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.util.integration.MutableIntegral;
import me.retrodaredevil.solarthing.util.integration.TrapezoidalRuleAccumulator;

import java.util.*;

public class DailyFXListUpdater implements PacketListUpdater {

	private final Map<Identifier, ListUpdater> map = new HashMap<>();

	@Override
	public void updatePackets(List<Packet> packets) {
		for(Packet packet : new ArrayList<>(packets)){
			if(packet instanceof DocumentedPacket){
				if(((DocumentedPacket<?>) packet).getPacketType() == SolarPacketType.FX_STATUS){
					FXStatusPacket fx = (FXStatusPacket) packet;
					Identifier identifier = fx.getIdentifier();
					ListUpdater updater = map.get(identifier);
					if(updater == null){
						updater = new ListUpdater();
						map.put(identifier, updater);
					}
					updater.update(packets, fx);
				}
			}
		}
	}
	private static MutableIntegral createMutableIntegral(){
		return new TrapezoidalRuleAccumulator();
	}
	private static final class ListUpdater {
		private Float minimumBatteryVoltage = null;
		private Float maximumBatteryVoltage = null;

		private final MutableIntegral inverterWH = createMutableIntegral();
		private final MutableIntegral chargerWH = createMutableIntegral();
		private final MutableIntegral buyWH = createMutableIntegral();
		private final MutableIntegral sellWH = createMutableIntegral();

		private final Set<Integer> operationalModeValues = new HashSet<>();
		private int errorMode = 0;
		private int warningMode = 0;
		private int misc = 0;
		private final Set<Integer> acModeValues = new HashSet<>();

		private void update(List<? super Packet> packets, FXStatusPacket fx){
			double hours = getHours();

			float batteryVoltage = fx.getBatteryVoltage();
			Float currentMin = minimumBatteryVoltage;
			Float currentMax = maximumBatteryVoltage;
			if(currentMin == null || batteryVoltage < currentMin){
				minimumBatteryVoltage = batteryVoltage;
			}
			if(currentMax == null || batteryVoltage > currentMax){
				maximumBatteryVoltage = batteryVoltage;
			}

			inverterWH.add(hours, fx.getInverterWattage());
			chargerWH.add(hours, fx.getChargerWattage());
			buyWH.add(hours, fx.getBuyWattage());
			sellWH.add(hours, fx.getSellWattage());

			operationalModeValues.add(fx.getOperationalModeValue());
			errorMode |= fx.getErrorMode();
			warningMode |= fx.getWarningMode();
			misc |= fx.getMisc();
			acModeValues.add(fx.getACModeValue());
			Packet packet = new ImmutableDailyFXPacket(
					minimumBatteryVoltage, maximumBatteryVoltage,
					(float) (inverterWH.getIntegral() / 1000), (float) (chargerWH.getIntegral() / 1000),
					(float) (buyWH.getIntegral() / 1000), (float) (sellWH.getIntegral() / 1000),
					operationalModeValues, errorMode, warningMode, misc, acModeValues,
					fx.getIdentifier()
			);
			packets.add(packet);
		}

		private void reset(){
			inverterWH.reset();
			chargerWH.reset();
			buyWH.reset();
			sellWH.reset();

			minimumBatteryVoltage = null;
			maximumBatteryVoltage = null;

			operationalModeValues.clear();
			errorMode = 0;
			acModeValues.clear();
			misc = 0;
			warningMode = 0;
		}

		private double getHours(){
			return System.currentTimeMillis() / (1000.0 * 60 * 60);
		}
	}
}
