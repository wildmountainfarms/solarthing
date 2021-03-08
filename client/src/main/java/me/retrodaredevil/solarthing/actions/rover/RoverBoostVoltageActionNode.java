package me.retrodaredevil.solarthing.actions.rover;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.PacketGroupProvider;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestPacketGroupEnvironment;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonTypeName("roverboostvoltage")
public class RoverBoostVoltageActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(RoverBoostVoltageActionNode.class);

	private final int boostVoltageRaw;
	private final boolean not;
	private final Integer fragmentId;

	@JsonCreator
	public RoverBoostVoltageActionNode(
			@JsonProperty(value = "voltageraw", required = true) int boostVoltageRaw,
			@JsonProperty("not") Boolean not,
			@JsonProperty("fragment") Integer fragmentId) {
		this.boostVoltageRaw = boostVoltageRaw;
		this.not = Boolean.TRUE.equals(not);
		this.fragmentId = fragmentId;
	}
	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		LatestPacketGroupEnvironment latestPacketGroupEnvironment = actionEnvironment.getInjectEnvironment().get(LatestPacketGroupEnvironment.class);
		PacketGroupProvider packetGroupProvider = latestPacketGroupEnvironment.getPacketGroupProvider();
		return new SimpleAction(false) {
			@Override
			protected void onUpdate() {
				super.onUpdate();
				PacketGroup packetGroup = packetGroupProvider.getPacketGroup();
				final FragmentedPacketGroup fragmentedPacketGroup;
				if (packetGroup instanceof FragmentedPacketGroup) {
					fragmentedPacketGroup = (FragmentedPacketGroup) packetGroup;
				} else {
					fragmentedPacketGroup = PacketGroups.parseToInstancePacketGroup(packetGroup, DefaultInstanceOptions.DEFAULT_DEFAULT_INSTANCE_OPTIONS);
				}
				RoverStatusPacket roverStatusPacket = null;
				for (Packet packet : fragmentedPacketGroup.getPackets()) {
					if (fragmentId != null && fragmentedPacketGroup.getFragmentId(packet) != fragmentId) {
						continue;
					}
					if (packet instanceof RoverStatusPacket) {
						roverStatusPacket = (RoverStatusPacket) packet;
						break;
					}
				}
				if (roverStatusPacket == null) {
					if (fragmentId == null) {
						LOGGER.warn("Couldn't find rover status packet!");
					} else {
						LOGGER.warn("Couldn't find rover status packet with fragment ID: " + fragmentId + "!");
					}
					return;
				}
				setDone((roverStatusPacket.getBoostChargingVoltageRaw() == boostVoltageRaw) != not);
			}
		};
	}
}
