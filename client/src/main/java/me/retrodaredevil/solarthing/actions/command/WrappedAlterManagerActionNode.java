package me.retrodaredevil.solarthing.actions.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.convenient.SingleActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;

import java.nio.file.Path;

/**
 * Providing a fragment for something in the automation program is not something you would be expecting to see.
 * The fragment ID is needed to identify the security event packets being uploaded. It is recommended to use a fragment ID in the 5000s
 */
@JsonTypeName("alter_manager")
public class WrappedAlterManagerActionNode implements ActionNode {

	private final SingleActionNode actionNode;

	public WrappedAlterManagerActionNode(
			@JsonProperty(value = "sender", required = true) String sender,
			@JsonProperty(value = "key_directory", required = true) Path keyDirectory,
			@JsonProperty(value = "fragment", required = true) int fragmentId
	) {
		actionNode = SingleActionNode.create(new AlterManagerActionNode(sender, keyDirectory, fragmentId));
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return actionNode.createAction(actionEnvironment);
	}
}
