package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;

@JsonTypeName("call")
public class CallActionNode implements ActionNode {
	private final String name;
	private final ActionEnvironment actionEnvironment;
	public CallActionNode(@JsonProperty("name") String name, @JacksonInject("environment") ActionEnvironment actionEnvironment) {
		this.name = name;
		this.actionEnvironment = actionEnvironment;
	}

	@Override
	public Action createAction() {
		return Actions.createDynamicActionRunner(() -> actionEnvironment.getDeclaredAction(name).createAction());
	}
}
