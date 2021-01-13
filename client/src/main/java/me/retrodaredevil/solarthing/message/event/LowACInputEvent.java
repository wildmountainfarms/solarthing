package me.retrodaredevil.solarthing.message.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.solar.outback.OutbackUtil;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.MiscMode;

import java.time.Duration;

import static java.util.Objects.requireNonNull;

@JsonTypeName("lowacinput")
public class LowACInputEvent extends GracePeriodTimeoutEvent {
	private final int lowThresholdVoltage;
	private final boolean lowRaw;
	private final int highThresholdVoltage;
	private final boolean highRaw;

	@JsonCreator
	public LowACInputEvent(
			@JsonProperty(value = "grace_period", required = true) String gracePeriodDurationString,
			@JsonProperty(value = "timeout", required = true) String timeoutDurationString,
			@JsonProperty(value = "low_threshold") Integer lowThreshold,
			@JsonProperty(value = "low_threshold_raw") Integer lowThresholdRaw,
			@JsonProperty(value = "high_threshold") Integer highThreshold,
			@JsonProperty(value = "high_threshold_raw") Integer highThresholdRaw
	) {
		super(gracePeriodDurationString, timeoutDurationString);
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
	protected @Nullable Runnable createDesiredTrigger(MessageSender sender, FragmentedPacketGroup previous, FragmentedPacketGroup current) {
		FXStatusPacket fx = OutbackUtil.getMasterFX(current);
		if (fx != null) {
			boolean is240 = fx.getMiscModes().contains(MiscMode.FX_230V_UNIT);
			int lowThreshold = is240 && lowRaw ? 2 * lowThresholdVoltage : lowThresholdVoltage;
			int highThreshold = is240 && highRaw ? 2 * highThresholdVoltage : highThresholdVoltage;
			int inputVoltage = fx.getInputVoltage();
			if (inputVoltage >= lowThreshold && inputVoltage <= highThreshold) {
				return () -> sender.sendMessage("Low AC Input Voltage! " + inputVoltage + "V (" + getPrettyDurationString() + ")");
			}
		}
		return null;
	}
}
