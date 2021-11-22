package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestFragmentedPacketGroupEnvironment;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.solar.outback.mx.ChargerMode;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.ChargingState;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import me.retrodaredevil.solarthing.solar.tracer.TracerStatusPacket;
import me.retrodaredevil.solarthing.solar.tracer.mode.ChargingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@JsonTypeName("fulloutput")
public class RequireFullOutputActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(RequireFullOutputActionNode.class);
	private final Map<Integer, List<String>> requiredIdentifierMap;
	private final Map<Integer, List<String>> ignoreReportedMXFloatMap;
	private final boolean log;

	@JsonCreator
	public RequireFullOutputActionNode(
			@JsonProperty(value = "required", required = true) Map<Integer, List<String>> requiredIdentifierMap,
			@JsonProperty("mx_float_ignore") Map<Integer, List<String>> ignoreReportedMXFloatMap,
			@JsonProperty(value = "log") Boolean log) {
		this.requiredIdentifierMap = requiredIdentifierMap;
		this.ignoreReportedMXFloatMap = ignoreReportedMXFloatMap == null ? Collections.emptyMap() : ignoreReportedMXFloatMap;
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
								// Some old MX firmwares consistently report being in float mode after a full absorb cycle even after the daily reset.
								//   This configuration option allows us to treat float as full output for a given MX
								boolean canIgnoreFloatMode = ignoreReportedMXFloatMap.getOrDefault(fragmentId, Collections.emptyList()).contains(desiredIdentifierRepresentation);
								if (mode != ChargerMode.SILENT && mode != ChargerMode.BULK && (!canIgnoreFloatMode || mode != ChargerMode.FLOAT)) {
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
							} else if (packet instanceof TracerStatusPacket) {
								TracerStatusPacket tracer = (TracerStatusPacket) packet;
								ChargingStatus status = tracer.getChargingMode();
								// we currently cannot tell if while in BOOST or EQUALIZE if the controller is actually in Bulk, so float is the only mode we know that isn't at full output
								if (status == ChargingStatus.FLOAT) {
									if (log) {
										LOGGER.info(identifier.getRepresentation() + " on fragment " + fragmentId + " is in mode " + status.getModeName());
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
		LatestFragmentedPacketGroupEnvironment latestPacketGroupEnvironment = actionEnvironment.getInjectEnvironment().get(LatestFragmentedPacketGroupEnvironment.class);
		return new SimpleAction(false) {
			@Override
			protected void onUpdate() {
				super.onUpdate();
				FragmentedPacketGroup latestPacketGroup = latestPacketGroupEnvironment.getFragmentedPacketGroupProvider().getPacketGroup();

				if (latestPacketGroup != null) {
					setDone(isFullOutput(latestPacketGroup));
				}
			}
		};
	}
}
