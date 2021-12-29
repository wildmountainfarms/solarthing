package me.retrodaredevil.solarthing.analytics;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;

public class RoverAnalyticsHandler implements PacketHandler {
	private final AnalyticsManager analyticsManager;
	private final AnalyticsTimer timer = new AnalyticsTimer();

	public RoverAnalyticsHandler(AnalyticsManager analyticsManager) {
		this.analyticsManager = analyticsManager;
	}

	@Override
	public void handle(PacketCollection packetCollection) {
		if (timer.shouldSend()) {
			timer.onSend();
			send(packetCollection);
		}
	}
	private void send(PacketCollection packetCollection) {
		for (Packet packet : packetCollection.getPackets()) {
			if (packet instanceof RoverStatusPacket) {
				RoverStatusPacket rover = (RoverStatusPacket) packet;
				String data = "(" + rover.getNumber() + ") " + rover.getProductModel() + "," + rover.getRatedChargingCurrentValue();
				if (!rover.supportsMesLoad()) {
					data += ",no MES Load";
				}
				analyticsManager.sendRoverStatus(data, timer.getUptimeHours());
			}
		}
	}
}
