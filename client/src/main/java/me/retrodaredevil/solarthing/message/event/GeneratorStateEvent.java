package me.retrodaredevil.solarthing.message.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.solar.outback.OutbackUtil;
import me.retrodaredevil.solarthing.solar.outback.fx.ACMode;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;

@JsonTypeName("generatorstate")
public class GeneratorStateEvent implements MessageEvent {
	private Boolean generatorOn = null;

	@Override
	public void run(MessageSender sender, FragmentedPacketGroup previous, FragmentedPacketGroup current) {
		FXStatusPacket fx = OutbackUtil.getMasterFX(current);
		ACMode mode = fx.getACMode();
		final Boolean wasGeneratorOn = this.generatorOn;
		if (mode == ACMode.NO_AC) {
			this.generatorOn = false;
			if (Boolean.TRUE.equals(wasGeneratorOn)) {
				sender.sendMessage("Generator has turned off!");
			}
		} else if (mode == ACMode.AC_USE) {
			this.generatorOn = true;
			if (Boolean.FALSE.equals(wasGeneratorOn)) {
				sender.sendMessage("Generator power being used!");
			}
		}
	}
}
