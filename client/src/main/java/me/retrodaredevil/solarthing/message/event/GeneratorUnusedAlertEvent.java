package me.retrodaredevil.solarthing.message.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.solar.outback.fx.ACMode;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;

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
		// We use a for loop here instead of just the "master" FX packet because it's possible for the voltage
		//    on one FX to be greater than another, so we want to check to see if this applied to *any* FXs
		for (Packet packet : current.getPackets()) {
			if (packet instanceof FXStatusPacket fx) {
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
		}
		return null;
	}
}
