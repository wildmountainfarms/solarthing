package me.retrodaredevil.solarthing.message.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.solar.outback.OutbackUtil;
import me.retrodaredevil.solarthing.solar.outback.fx.ACMode;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;

import java.util.Collections;
import java.util.List;

@JsonTypeName("acmodechange")
public class ACModeChangeEvent implements MessageEvent {
	private final List<ACMode> changeTo;
	private final List<ACMode> changeFrom;

	@JsonCreator
	public ACModeChangeEvent(
			@JsonProperty(value = "to") List<ACMode> changeTo,
			@JsonProperty(value = "from") List<ACMode> changeFrom
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

		if (previousFX.getACMode() != currentFX.getACMode()) {
			if (changeTo.contains(currentFX.getACMode()) || changeFrom.contains(previousFX.getACMode())) {
				sender.sendMessage(currentFX.getACMode().getModeName());
			}
		}
	}
}
