package me.retrodaredevil.solarthing.analytics;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.solar.renogy.ProductModelUtil;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;

public class RoverAnalyticsHandler implements PacketHandler {
	private final AnalyticsManager analyticsManager;
	private final AnalyticsTimer timer = new AnalyticsTimer();

	public RoverAnalyticsHandler(AnalyticsManager analyticsManager) {
		this.analyticsManager = analyticsManager;
	}

	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) {
		if (!wasInstant) {
			return;
		}
		if (timer.shouldSend()) {
			timer.onSend();
			send(packetCollection);
		}
	}
	private void send(PacketCollection packetCollection) {
		for (Packet packet : packetCollection.getPackets()) {
			if (packet instanceof RoverStatusPacket) {
				RoverStatusPacket rover = (RoverStatusPacket) packet;
				String data = "";
				String model = rover.getProductModel(); // this may be sensitive info, so don't send it directly.
				if (ProductModelUtil.isRover(model)) {
					data += "rover";
				} else if (ProductModelUtil.isWanderer(model)) {
					data += "wanderer";
				} else if (ProductModelUtil.isComet(model)) {
					data += "comet";
				} else if (ProductModelUtil.isZenith(model)) {
					data += "zenith";
				} else if (model.toLowerCase().contains("chc")) {
					data += "chc?";
				} else if (model.toLowerCase().contains("mppt")) {
					data += "MPPT?";
				} else if (model.toLowerCase().contains("pwm")) {
					data += "PWM?";
				} else {
					data += "unknown";
				}
				data += "," + rover.getRatedChargingCurrentValue();
				analyticsManager.sendRoverStatus(data, timer.getUptimeHours());
				return;
			}
		}
	}
}
