package me.retrodaredevil.solarthing.actions.rover;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.solarthing.PacketGroupProvider;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
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
	// TODO maybe also include a "number" field. (Multiple rovers can be on a single fragment)

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
				if (packetGroup == null) {
					LOGGER.warn("packetGroup is null!");
					return;
				}
				// Really we should be using a LatestFragmentPacketGroupEnvironment for some cases, and not for others, but instead we just do two different things based on the info given. This seems to work...
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
