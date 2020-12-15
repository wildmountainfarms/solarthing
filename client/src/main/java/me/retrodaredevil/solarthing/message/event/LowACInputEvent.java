package me.retrodaredevil.solarthing.message.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.solar.outback.OutbackUtil;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;

import java.time.Duration;

@JsonTypeName("lowacinput")
public class LowACInputEvent implements MessageEvent {
	private final long gracePeriod;
	private final long timeout;

	private Long startTime = null;
	private Long lastSend = null;

	@JsonCreator
	public LowACInputEvent(
			@JsonProperty(value = "grace_period", required = true) String gracePeriodDurationString,
			@JsonProperty(value = "timeout", required = true) String timeoutDurationString
	) {
		this.gracePeriod = Duration.parse(gracePeriodDurationString).toMillis();
		this.timeout = Duration.parse(timeoutDurationString).toMillis();
	}

	@Override
	public void run(MessageSender sender, FragmentedPacketGroup previous, FragmentedPacketGroup current) {
		FXStatusPacket fx = OutbackUtil.getMasterFX(current);
		if (fx != null) {
			int inputVoltageRaw = fx.getInputVoltageRaw();
			if (inputVoltageRaw != 0 && inputVoltageRaw < 100) {
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
						sender.sendMessage("Low AC Input Voltage! " + fx.getInputVoltage() + "V (" + seconds + " seconds)");
					}
				}
			} else {
				startTime = null;
			}
		}
	}
}
