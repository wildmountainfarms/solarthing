package me.retrodaredevil.solarthing.solar.outback.fx.supplementary;

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
		for(Packet packet : packets){
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
		private final MutableIntegral inverterWH = createMutableIntegral();
		private final MutableIntegral chargerWH = createMutableIntegral();
		private final MutableIntegral buyWH = createMutableIntegral();
		private final MutableIntegral sellWH = createMutableIntegral();

		private Float minimumBatteryVoltage = null;
		private Float maximumBatteryVoltage = null;

		private final Set<Integer> operationalModeValues = new HashSet<>();
		private int errorMode = 0;
		private final Set<Integer> acModeValues = new HashSet<>();
		private int misc = 0;
		private int warningMode = 0;

		private void update(List<? super Packet> packets, FXStatusPacket fx){
			double hours = getHours();
			inverterWH.add(hours, fx.getInverterWattage());
			chargerWH.add(hours, fx.getChargerWattage());
			buyWH.add(hours, fx.getBuyWattage());
			sellWH.add(hours, fx.getSellWattage());

			float batteryVoltage = fx.getBatteryVoltage();
			Float currentMin = minimumBatteryVoltage;
			Float currentMax = maximumBatteryVoltage;
			if(currentMin == null || batteryVoltage < currentMin){
				minimumBatteryVoltage = batteryVoltage;
			}
			if(currentMax == null || batteryVoltage > currentMax){
				maximumBatteryVoltage = batteryVoltage;
			}

			operationalModeValues.add(fx.getOperationalModeValue());
			errorMode |= fx.getErrorMode();
			acModeValues.add(fx.getACModeValue());
			misc |= fx.getMisc();
			warningMode |= fx.getWarningMode();
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
