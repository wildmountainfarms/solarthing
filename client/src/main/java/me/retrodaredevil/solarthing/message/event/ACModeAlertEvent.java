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

import java.util.List;

@JsonTypeName("acmodealert")
public class ACModeAlertEvent extends GracePeriodTimeoutEvent {
	private final List<ACMode> modes;
	@JsonCreator
	public ACModeAlertEvent(
			@JsonProperty(value = "grace_period", required = true) String gracePeriodDurationString,
			@JsonProperty(value = "timeout", required = true) String timeoutDurationString,
			@JsonProperty(value = "modes", required = true) List<ACMode> modes
	) {
		super(gracePeriodDurationString, timeoutDurationString);
		this.modes = modes;
	}
	@Override
	protected @Nullable Runnable createDesiredTrigger(MessageSender sender, FragmentedPacketGroup previous, FragmentedPacketGroup current) {
		FXStatusPacket fx = OutbackUtil.getMasterFX(current);
		if (fx != null) {
			ACMode currentMode = fx.getACMode();
			if (modes.contains(currentMode)) {
				return () -> sender.sendMessage("Been in " + currentMode.getModeName() + " for " + getPrettyDurationString() + "!");
			}
		}
		return null;
	}
}
