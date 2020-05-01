package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;

@JsonTypeName("unlock")
public class UnlockActionNode implements ActionNode {
	private final String lockName;
	private final ActionEnvironment actionEnvironment;

	public UnlockActionNode(@JsonProperty("name") String lockName, @JacksonInject("environment") ActionEnvironment actionEnvironment) {
		this.lockName = lockName;
		this.actionEnvironment = actionEnvironment;
	}

	@Override
	public Action createAction() {
		return Actions.createRunOnce(() -> actionEnvironment.unlock(lockName));
	}
}
