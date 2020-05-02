package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;

@JsonTypeName("unlock")
public class UnlockActionNode implements ActionNode {
	private final String lockName;

	public UnlockActionNode(@JsonProperty("name") String lockName) {
		this.lockName = lockName;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return Actions.createRunOnce(() -> actionEnvironment.getGlobalEnvironment().unlock(lockName));
	}
}
