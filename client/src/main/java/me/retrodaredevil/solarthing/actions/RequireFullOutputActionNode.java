package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestPacketGroupEnvironment;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.solar.outback.mx.ChargerMode;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.ChargingState;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@JsonTypeName("fulloutput")
public class RequireFullOutputActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(RequireFullOutputActionNode.class);
	private final Map<Integer, List<String>> requiredIdentifierMap;
	private final boolean log;

	@JsonCreator
	public RequireFullOutputActionNode(
			@JsonProperty(value = "required", required = true) Map<Integer, List<String>> requiredIdentifierMap,
			@JsonProperty(value = "log") Boolean log) {
		this.requiredIdentifierMap = requiredIdentifierMap;
		this.log = log == null || log;
	}

	private boolean isFullOutput(FragmentedPacketGroup latestPacketGroup) {
		for (Map.Entry<Integer, List<String>> entry : requiredIdentifierMap.entrySet()) {
			int desiredFragmentId = entry.getKey();
			if (!latestPacketGroup.hasFragmentId(desiredFragmentId)) {
				continue;
			}
			for (String desiredIdentifierRepresentation : entry.getValue()) {
				for (Packet packet : latestPacketGroup.getPackets()) {
					int fragmentId = latestPacketGroup.getFragmentId(packet);
					if (fragmentId != desiredFragmentId) {
						continue;
					}
					if (packet instanceof Identifiable) {
						Identifier identifier = ((Identifiable) packet).getIdentifier();
						if (desiredIdentifierRepresentation.equals(identifier.getRepresentation())) {
							if (packet instanceof MXStatusPacket) {
								MXStatusPacket mx = (MXStatusPacket) packet;
								ChargerMode mode = mx.getChargingMode();
								if (mode != ChargerMode.SILENT && mode != ChargerMode.BULK) {
									if (log) {
										LOGGER.info(identifier.getRepresentation() + " on fragment " + fragmentId + " is in mode " + mode.getModeName());
									}
									return false;
								}
							} else if (packet instanceof RoverStatusPacket) {
								RoverStatusPacket rover = (RoverStatusPacket) packet;
								ChargingState state = rover.getChargingMode();
								if (state != ChargingState.ACTIVATED && state != ChargingState.DEACTIVATED && state != ChargingState.MPPT) {
									if (log) {
										LOGGER.info(identifier.getRepresentation() + " on fragment " + fragmentId + " is in mode " + state.getModeName());
									}
									return false;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		LatestPacketGroupEnvironment latestPacketGroupEnvironment = actionEnvironment.getInjectEnvironment().get(LatestPacketGroupEnvironment.class);
		return new SimpleAction(false) {
			@Override
			protected void onUpdate() {
				super.onUpdate();
				FragmentedPacketGroup latestPacketGroup = (FragmentedPacketGroup) latestPacketGroupEnvironment.getPacketGroupProvider().getPacketGroup();

				setDone(isFullOutput(latestPacketGroup));
			}
		};
	}
}
