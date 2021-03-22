package me.retrodaredevil.solarthing.message.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.solar.outback.OutbackUtil;
import me.retrodaredevil.solarthing.solar.outback.fx.ACMode;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.MiscMode;

@JsonTypeName("generatorunusedalert")
public class GeneratorUnusedAlertEvent extends GracePeriodTimeoutEvent {
	private final int lowThresholdVoltage;
	private final boolean lowRaw;

	@JsonCreator
	public GeneratorUnusedAlertEvent(
			@JsonProperty(value = "grace_period", required = true) String gracePeriodDurationString,
			@JsonProperty(value = "timeout", required = true) String timeoutDurationString,
			@JsonProperty(value = "voltage_threshold") Integer lowThreshold,
			@JsonProperty(value = "voltage_threshold_raw") Integer lowThresholdRaw
	) {
		super(gracePeriodDurationString, timeoutDurationString);
		if (lowThreshold != null) {
			lowThresholdVoltage = lowThreshold;
			lowRaw = false;
		} else if (lowThresholdRaw != null) {
			lowThresholdVoltage = lowThresholdRaw;
			lowRaw = true;
		} else {
			lowThresholdVoltage = 16;
			lowRaw = true;
		}
	}

	@Override
	protected @Nullable Runnable createDesiredTrigger(MessageSender sender, FragmentedPacketGroup previous, FragmentedPacketGroup current) {
		FXStatusPacket fx = OutbackUtil.getMasterFX(current);
		if (fx != null) {
			boolean is230 = fx.is230V();
			int lowThreshold = is230 && lowRaw ? 2 * lowThresholdVoltage : lowThresholdVoltage;
			int inputVoltage = fx.getInputVoltage();
			if (fx.getACMode() == ACMode.AC_DROP) {
				return () -> sender.sendMessage("Generator dropping power! (on and not using for " + getPrettyDurationString() + ")");
			}
			if (inputVoltage >= lowThreshold && fx.getACMode() != ACMode.AC_USE) {
				return () -> sender.sendMessage("Low Generator Voltage! " + inputVoltage + "V (on and not using for " + getPrettyDurationString() + ")");
			}
		}
		return null;
	}
}
