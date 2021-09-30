package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestFragmentedPacketGroupEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestPacketGroupEnvironment;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.util.IdentifierUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@JsonTypeName("required")
public class RequiredIdentifierActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(RequiredIdentifierActionNode.class);
	private final Map<Integer, List<String>> requiredIdentifierMap;
	private final boolean log;

	@JsonCreator
	public RequiredIdentifierActionNode(
			@JsonProperty(value = "required", required = true) Map<Integer, List<String>> requiredIdentifierMap,
			@JsonProperty(value = "log") Boolean log) {
		this.requiredIdentifierMap = requiredIdentifierMap;
		this.log = log == null || log;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		LatestFragmentedPacketGroupEnvironment latestPacketGroupEnvironment = actionEnvironment.getInjectEnvironment().get(LatestFragmentedPacketGroupEnvironment.class);
		return new SimpleAction(false) {
			@Override
			protected void onUpdate() {
				super.onUpdate();
				FragmentedPacketGroup packetGroup = latestPacketGroupEnvironment.getFragmentedPacketGroupProvider().getPacketGroup();
				if (packetGroup == null) {
					return;
				}
				String reason = IdentifierUtil.getRequirementNotMetReason(requiredIdentifierMap, packetGroup);
				if (reason != null) {
					if (log) {
						LOGGER.info("Requirement not met: " + reason);
					}
				} else {
					setDone(true);
				}
			}
		};
	}
}
