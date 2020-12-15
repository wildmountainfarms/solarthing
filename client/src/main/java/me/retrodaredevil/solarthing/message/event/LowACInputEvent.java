package me.retrodaredevil.solarthing.message.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.solar.outback.OutbackUtil;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.MiscMode;

import java.time.Duration;

@JsonTypeName("lowacinput")
public class LowACInputEvent implements MessageEvent {
	private final long gracePeriod;
	private final long timeout;
	private final int lowThresholdVoltage;
	private final boolean lowRaw;
	private final int highThresholdVoltage;
	private final boolean highRaw;

	private Long startTime = null;
	private Long lastSend = null;

	@JsonCreator
	public LowACInputEvent(
			@JsonProperty(value = "grace_period", required = true) String gracePeriodDurationString,
			@JsonProperty(value = "timeout", required = true) String timeoutDurationString,
			@JsonProperty(value = "low_threshold") Integer lowThreshold,
			@JsonProperty(value = "low_threshold_raw") Integer lowThresholdRaw,
			@JsonProperty(value = "high_threshold") Integer highThreshold,
			@JsonProperty(value = "high_threshold_raw") Integer highThresholdRaw
	) {
		this.gracePeriod = Duration.parse(gracePeriodDurationString).toMillis();
		this.timeout = Duration.parse(timeoutDurationString).toMillis();
		if (lowThreshold != null) {
			lowThresholdVoltage = lowThreshold;
			lowRaw = false;
		} else if (lowThresholdRaw != null) {
			lowThresholdVoltage = lowThresholdRaw;
			lowRaw = true;
		} else {
			lowThresholdVoltage = 2;
			lowRaw = true;
		}

		if (highThreshold != null) {
			highThresholdVoltage = highThreshold;
			highRaw = false;
		} else if (highThresholdRaw != null) {
			highThresholdVoltage = highThresholdRaw;
			highRaw = true;
		} else {
			highThresholdVoltage = 100;
			highRaw = true;
		}
	}

	@Override
	public void run(MessageSender sender, FragmentedPacketGroup previous, FragmentedPacketGroup current) {
		FXStatusPacket fx = OutbackUtil.getMasterFX(current);
		if (fx != null) {
			boolean is240 = fx.getMiscModes().contains(MiscMode.FX_230V_UNIT);
			int lowThreshold = is240 && lowRaw ? 2 * lowThresholdVoltage : lowThresholdVoltage;
			int highThreshold = is240 && highRaw ? 2 * highThresholdVoltage : highThresholdVoltage;
			int inputVoltage = fx.getInputVoltage();
			if (inputVoltage >= lowThreshold && inputVoltage <= highThreshold) {
				final Long startTime = this.startTime;
				long now = System.currentTimeMillis();
				if (startTime == null) {
					this.startTime = now;
				} else if (startTime + gracePeriod < now) {
					final Long lastSend = this.lastSend;
					if (lastSend == null || lastSend + timeout < now) {
						this.lastSend = now;
						long timeMillis = now - startTime;
						long seconds = Math.round(timeMillis / 1000.0);
						sender.sendMessage("Low AC Input Voltage! " + inputVoltage + "V (" + seconds + " seconds)");
					}
				}
			} else {
				startTime = null;
			}
		}
	}
}
