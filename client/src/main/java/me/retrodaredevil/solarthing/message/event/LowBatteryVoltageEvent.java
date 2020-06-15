package me.retrodaredevil.solarthing.message.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@JsonTypeName("lowbatteryvoltage")
public class LowBatteryVoltageEvent implements MessageEvent {
	private static final NumberFormat FORMAT = new DecimalFormat("0.0");
	private final float batteryVoltage;
	private final long timeoutMillis;

	private Long lastSend = null;

	@JsonCreator
	public LowBatteryVoltageEvent(
			@JsonProperty(value = "voltage", required = true) float batteryVoltage,
			@JsonProperty(value = "timeout", required = true) float timeoutMinutes
	) {
		this.batteryVoltage = batteryVoltage;
		this.timeoutMillis = Math.round(timeoutMinutes * 60 * 1000);
	}

	@Override
	public void run(MessageSender sender, FragmentedPacketGroup previous, FragmentedPacketGroup current) {
		Float currentBatteryVoltage = null;
		for (Packet packet : current.getPackets()) {
			if (packet instanceof BatteryVoltage) {
				float voltage = ((BatteryVoltage) packet).getBatteryVoltage();
				if (voltage - 0.001f <= batteryVoltage) {
					currentBatteryVoltage = voltage;
					break;
				}
			}
		}
		if (currentBatteryVoltage != null) {
			final Long lastSend = this.lastSend;
			long now = System.currentTimeMillis();
			if (lastSend == null || lastSend + timeoutMillis <= now) {
				this.lastSend = now;
				sender.sendMessage("Low Battery: " + FORMAT.format(currentBatteryVoltage));
			}
		}
	}
}
