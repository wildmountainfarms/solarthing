package me.retrodaredevil.solarthing.actions.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.convenient.SingleActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;

import java.io.File;

@JsonTypeName("alter_manager")
public class WrappedAlterManagerActionNode implements ActionNode {

	private final SingleActionNode actionNode;

	public WrappedAlterManagerActionNode(
			@JsonProperty(value = "sender", required = true) String sender,
			@JsonProperty(value = "key_directory", required = true) File keyDirectory
	) {
		actionNode = SingleActionNode.create(new AlterManagerActionNode(sender, keyDirectory));
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return actionNode.createAction(actionEnvironment);
	}
}
