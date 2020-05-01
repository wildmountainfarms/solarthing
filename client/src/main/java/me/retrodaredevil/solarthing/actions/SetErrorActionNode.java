package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;

@JsonTypeName("seterror")
public class SetErrorActionNode implements ActionNode {
	private final String error;
	private final ActionEnvironment actionEnvironment;

	public SetErrorActionNode(@JsonProperty("error") String error, @JacksonInject("environment") ActionEnvironment actionEnvironment) {
		this.error = error;
		this.actionEnvironment = actionEnvironment;
	}

	@Override
	public Action createAction() {
		return Actions.createRunOnce(() -> actionEnvironment.setError(error));
	}
}
