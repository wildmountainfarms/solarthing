package me.retrodaredevil.action.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;

import static java.util.Objects.requireNonNull;

/**
 * Similar to {@link CallActionNode}, except whatever scope this is called from is used to create the action
 */
@JsonTypeName("jump")
public class JumpActionNode implements ActionNode {
	private final String name;
	public JumpActionNode(@JsonProperty("name") String name) {
		this.name = requireNonNull(name);
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		VariableEnvironment variableEnvironment = actionEnvironment.getVariableEnvironment();
		return Actions.createDynamicActionRunner(() -> variableEnvironment.getDeclaredAction(name).getActionNode().createAction(actionEnvironment));
	}
}
