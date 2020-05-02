package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;

@JsonTypeName("call")
public class CallActionNode implements ActionNode {
	private final String name;
	public CallActionNode(@JsonProperty("name") String name) {
		this.name = name;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return Actions.createDynamicActionRunner(() -> actionEnvironment.getLocalEnvironment().getDeclaredAction(name).createAction(actionEnvironment));
	}
}
