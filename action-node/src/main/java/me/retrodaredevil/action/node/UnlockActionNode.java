package me.retrodaredevil.action.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@JsonTypeName("unlock")
@NullMarked
public class UnlockActionNode implements ActionNode {

	private final ActionNode actionNode;

	public UnlockActionNode(
			@JsonProperty(value = "name", required = true) String lockName,
			@JsonProperty(value = "set") @Nullable String lockSetName
	) {
		actionNode = new LockActionNode(lockName, lockSetName, true);
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return actionNode.createAction(actionEnvironment);
	}
}
