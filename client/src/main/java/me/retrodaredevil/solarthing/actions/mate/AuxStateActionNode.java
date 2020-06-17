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
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.MiscMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonTypeName("auxstate")
public class AuxStateActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuxStateActionNode.class);
	private final boolean on;

	@JsonCreator
	public AuxStateActionNode(@JsonProperty(value = "on", required = true) boolean on) {
		this.on = on;
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
					setDone(fxStatusPacket.getMiscModes().contains(MiscMode.AUX_OUTPUT_ON) == on);
				}
			}
		};

	}
}
