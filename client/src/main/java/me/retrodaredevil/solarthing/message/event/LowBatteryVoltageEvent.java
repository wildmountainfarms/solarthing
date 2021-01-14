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
import java.time.Duration;

@JsonTypeName("lowbatteryvoltage")
public class LowBatteryVoltageEvent implements MessageEvent {
	private static final NumberFormat FORMAT = new DecimalFormat("0.0");
	private final float batteryVoltage;
	private final long timeoutMillis;
	private final long belowForMillis;

	private Long belowStartTime = null;
	private Long lastSend = null;

	@JsonCreator
	public LowBatteryVoltageEvent(
			@JsonProperty(value = "voltage", required = true) float batteryVoltage,
			@JsonProperty(value = "timeout", required = true) String timeoutDurationString,
			@JsonProperty("time") String belowForDurationString // not required. // Uses duration because if seconds are used, automatically parsed into seconds
	) {
		this.batteryVoltage = batteryVoltage;
		this.timeoutMillis = Duration.parse(timeoutDurationString).toMillis();
		this.belowForMillis = belowForDurationString == null ? 0 : Duration.parse(timeoutDurationString).toMillis();
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
			long now = System.currentTimeMillis();
			if (belowStartTime == null) {
				belowStartTime = now;
			}
			final long belowStartTime = this.belowStartTime;
			final Long lastSend = this.lastSend;
			if ((lastSend == null || lastSend + timeoutMillis <= now) && belowStartTime + belowForMillis <= now) {
				this.lastSend = now;
				long currentlyBelowForMillis = now - belowStartTime;
				String extra = "";
				if (currentlyBelowForMillis > 0) { // this means that belowForMillis != 0
					extra += " (" + TimeUtil.millisToPrettyString(currentlyBelowForMillis) + ")";
				}
				sender.sendMessage("Low Battery: " + FORMAT.format(currentBatteryVoltage) + "V (" + FORMAT.format(batteryVoltage) + "V warning)" + extra);
			}
		} else {
			belowStartTime = null;
		}
	}
}
