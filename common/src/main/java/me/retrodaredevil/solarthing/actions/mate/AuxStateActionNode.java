package me.retrodaredevil.solarthing.actions.mate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.solarthing.PacketGroupProvider;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestPacketGroupEnvironment;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.solar.outback.OutbackUtil;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An action that becomes done when aux is on or off, based on {@link #on}
 */
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
			/** A simple variable to prevent spamming of the log file*/
			private boolean hadFX = true;
			@Override
			protected void onUpdate() {
				super.onUpdate();
				PacketGroup packetGroup = packetGroupProvider.getPacketGroup();
				if (packetGroup == null) {
					LOGGER.warn("packetGroup is null!");
					return;
				}
				FXStatusPacket fxStatusPacket = OutbackUtil.getMasterFX(packetGroup);
				if (fxStatusPacket == null) {
					if (hadFX) {
						LOGGER.warn("No master FX Status Packet!");
					}
					hadFX = false;
				} else {
					setDone(fxStatusPacket.isAuxOn() == on);
					if (!hadFX) {
						LOGGER.info("We now have a master FX Status Packet");
					}
					hadFX = true;
				}
			}
		};

	}
}
