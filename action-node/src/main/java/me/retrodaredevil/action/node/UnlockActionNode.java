package me.retrodaredevil.action.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.environment.ActionEnvironment;

@JsonTypeName("unlock")
public class UnlockActionNode implements ActionNode {

	private final ActionNode actionNode;

	public UnlockActionNode(
			@JsonProperty(value = "name", required = true) String lockName,
			@JsonProperty(value = "set") String lockSetName
	) {
		actionNode = new LockActionNode(lockName, lockSetName, true);
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return actionNode.createAction(actionEnvironment);
	}
}
