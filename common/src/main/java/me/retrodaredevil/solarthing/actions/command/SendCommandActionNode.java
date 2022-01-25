package me.retrodaredevil.solarthing.actions.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.command.provider.RequestCommandPacketProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

@Deprecated
@JsonTypeName("sendcommand")
public class SendCommandActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(SendCommandActionNode.class);

	private final ActionNode actionNode;

	@JsonCreator
	public SendCommandActionNode(
			@JsonProperty(value = "directory", required = true) File keyDirectory,
			@JsonProperty(value = "sender", required = true) String sender,
			@JsonProperty(value = "targets", required = true) List<Integer> fragmentIdTargets,
			@JsonProperty(value = "command", required = true) String commandName) {
		actionNode = SendEncryptedActionNode.create(keyDirectory, sender, fragmentIdTargets, new RequestCommandPacketProvider(commandName));
		LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "You are using sendcommand. You should switch to using sendopen. This will be removed in the future.");
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return actionNode.createAction(actionEnvironment);
	}
}
