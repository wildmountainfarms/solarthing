package me.retrodaredevil.solarthing.actions.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.command.provider.RequestHeartbeatPacketProvider;
import me.retrodaredevil.solarthing.type.event.feedback.HeartbeatData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

@Deprecated
@JsonTypeName("sendrequestheartbeat")
public class SendRequestHeartbeatActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(SendRequestHeartbeatActionNode.class);

	private final ActionNode actionNode;

	@JsonCreator
	public SendRequestHeartbeatActionNode(
			@JsonProperty(value = "directory", required = true) File keyDirectory,
			@JsonProperty(value = "sender", required = true) String sender,
			@JsonProperty(value = "targets", required = true) List<Integer> fragmentIdTargets,
			@JsonProperty(value = "heartbeat", required = true) HeartbeatData heartbeatData) {
		actionNode = SendEncryptedActionNode.create(keyDirectory, sender, fragmentIdTargets, new RequestHeartbeatPacketProvider(heartbeatData));

		LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "You are using sendrequestheartbeat. You should switch to using sendopen. This will be removed in the future.");
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return actionNode.createAction(actionEnvironment);
	}
}
