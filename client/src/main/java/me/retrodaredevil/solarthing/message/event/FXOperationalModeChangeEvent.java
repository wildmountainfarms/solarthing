package me.retrodaredevil.solarthing.message.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.solar.outback.OutbackUtil;
import me.retrodaredevil.solarthing.solar.outback.fx.ACMode;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.OperationalMode;

import java.util.Collections;
import java.util.List;

@JsonTypeName("fxchange")
public class FXOperationalModeChangeEvent implements MessageEvent {
	private final List<OperationalMode> changeTo;
	private final List<OperationalMode> changeFrom;

	@JsonCreator
	public FXOperationalModeChangeEvent(
			@JsonProperty(value = "to") List<OperationalMode> changeTo,
			@JsonProperty(value = "from") List<OperationalMode> changeFrom
	) {
		this.changeTo = changeTo == null ? Collections.emptyList() : changeTo;
		this.changeFrom = changeFrom == null ? Collections.emptyList() : changeFrom;
	}

	@Override
	public void run(MessageSender sender, FragmentedPacketGroup previous, FragmentedPacketGroup current) {
		FXStatusPacket previousFX = OutbackUtil.getMasterFX(previous);
		FXStatusPacket currentFX = OutbackUtil.getMasterFX(current);
		if (previousFX == null || currentFX == null) {
			return;
		}

		if (previousFX.getOperationalMode() != currentFX.getOperationalMode()) {
			if (changeTo.contains(currentFX.getOperationalMode()) || changeFrom.contains(previousFX.getOperationalMode())) {
				sender.sendMessage("FX Mode: " + currentFX.getOperationalMode().getModeName());
			}
		}
	}
}
