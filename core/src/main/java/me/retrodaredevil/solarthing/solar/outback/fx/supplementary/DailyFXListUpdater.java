package me.retrodaredevil.solarthing.solar.outback.fx.supplementary;

import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.creation.PacketListUpdater;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.solar.SolarPacketType;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.util.integration.MutableIntegral;
import me.retrodaredevil.solarthing.util.integration.TrapezoidalRuleAccumulator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		private final MutableIntegral inverter = createMutableIntegral();
		private final MutableIntegral charger = createMutableIntegral();
		private final MutableIntegral buy = createMutableIntegral();
		private final MutableIntegral sell = createMutableIntegral();
		private void update(List<? super Packet> packets, FXStatusPacket fx){
			double hours = getHours();
			inverter.add(hours, fx.getInverterWattage());
			charger.add(hours, fx.getChargerWattage());
			buy.add(hours, fx.getBuyWattage());
			sell.add(hours, fx.getSellWattage());
		}

		private double getHours(){
			return System.currentTimeMillis() / (1000.0 * 60 * 60);
		}
	}
}
