package me.retrodaredevil.solarthing.actions.mate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.PacketGroupProvider;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestPacketGroupEnvironment;
import me.retrodaredevil.solarthing.solar.outback.OutbackUtil;
import me.retrodaredevil.solarthing.solar.outback.fx.ACMode;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonTypeName("acmode")
public class ACModeActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(ACModeActionNode.class);
	private final ACMode acMode;

	@JsonCreator
	public ACModeActionNode(@JsonProperty("mode") String mode) {
		this(parseMode(mode));
	}

	public ACModeActionNode(ACMode acMode) {
		this.acMode = acMode;
	}
	private static ACMode parseMode(String modeName) {
		modeName = modeName.replaceAll(" ", "").toLowerCase();
		switch (modeName) {
			case "noac":
				return ACMode.NO_AC;
			case "acdrop":
				return ACMode.AC_DROP;
			case "acuse":
				return ACMode.AC_USE;
		}
		throw new IllegalArgumentException("ACMode: '" + modeName + "' does not exist!");
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		LatestPacketGroupEnvironment latestPacketGroupEnvironment = actionEnvironment.getInjectEnvironment().get(LatestPacketGroupEnvironment.class);
		PacketGroupProvider packetGroupProvider = latestPacketGroupEnvironment.getPacketGroupProvider();
		return new SimpleAction(false) {
			@Override
			protected void onUpdate() {
				super.onUpdate();
				FXStatusPacket fxStatusPacket = OutbackUtil.getMasterFX(packetGroupProvider.getPacketGroup());
				if (fxStatusPacket == null) {
					LOGGER.warn("No master FX Status Packet!");
				} else {
					setDone(fxStatusPacket.getACMode() == acMode);
				}
			}
		};
	}
}
