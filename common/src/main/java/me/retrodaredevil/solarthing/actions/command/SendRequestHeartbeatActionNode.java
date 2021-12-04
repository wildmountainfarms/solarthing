package me.retrodaredevil.solarthing.actions.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableRequestHeartbeatPacket;
import me.retrodaredevil.solarthing.commands.util.CommandManager;
import me.retrodaredevil.solarthing.type.event.feedback.HeartbeatData;

import java.io.File;
import java.util.List;
import java.util.UUID;

@JsonTypeName("sendrequestheartbeat")
public class SendRequestHeartbeatActionNode implements ActionNode {
	private final ActionNode actionNode;

	@JsonCreator
	public SendRequestHeartbeatActionNode(
			@JsonProperty(value = "directory", required = true) File keyDirectory,
			@JsonProperty(value = "sender", required = true) String sender,
			@JsonProperty(value = "targets", required = true) List<Integer> fragmentIdTargets,
			@JsonProperty(value = "heartbeat", required = true) HeartbeatData heartbeatData) {
		actionNode = new SendEncryptedActionNode(new CommandManager(keyDirectory, sender), fragmentIdTargets, () -> new ImmutableRequestHeartbeatPacket(heartbeatData, UUID.randomUUID()));
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return actionNode.createAction(actionEnvironment);
	}
}
