package me.retrodaredevil.solarthing.message.event;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Duration;

import static java.util.Objects.requireNonNull;

@JsonTypeName("lowbatteryvoltage")
public class LowBatteryVoltageEvent extends GracePeriodTimeoutEvent {
	private static final NumberFormat FORMAT = new DecimalFormat("0.0");
	private final float batteryVoltage;

	@JsonCreator
	public LowBatteryVoltageEvent(
			@JsonProperty(value = "voltage", required = true) float batteryVoltage,
			@JsonProperty("grace_period") @JsonAlias("time") String belowForDurationString,
			@JsonProperty(value = "timeout", required = true) String timeoutDurationString
	) {
		super(belowForDurationString == null ? 0 : Duration.parse(belowForDurationString).toMillis(), Duration.parse(timeoutDurationString).toMillis());
		this.batteryVoltage = batteryVoltage;
	}

	@Override
	protected @Nullable Runnable createDesiredTrigger(MessageSender sender, FragmentedPacketGroup previous, FragmentedPacketGroup current) {
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
			String batteryVoltageString = FORMAT.format(currentBatteryVoltage);
			return () -> {
				String extra = "";
				long durationMillis = requireNonNull(getDurationMillis());
				if (durationMillis > 200) {
					extra += " (" + TimeUtil.millisToPrettyString(durationMillis) + ")";
				}
				String text = "Low Battery: " + batteryVoltageString + "V (" + FORMAT.format(batteryVoltage) + "V warning)" + extra;
				sender.sendMessage(text);
			};
		}
		return null;
	}
}
