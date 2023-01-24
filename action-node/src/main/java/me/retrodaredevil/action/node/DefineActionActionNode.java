package me.retrodaredevil.action.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;

import static java.util.Objects.requireNonNull;

@JsonTypeName("act")
public class DefineActionActionNode implements ActionNode {
	private final String name;
	private final ActionNode actionNode;

	@JsonCreator
	public DefineActionActionNode(
			@JsonProperty(value = "name", required = true) String name,
			@JsonProperty(value = "action", required = true) ActionNode actionNode) {
		this.name = requireNonNull(name);
		this.actionNode = requireNonNull(actionNode);
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		VariableEnvironment variableEnvironment = actionEnvironment.getVariableEnvironment();
		return Actions.createRunOnce(() -> {
			variableEnvironment.setDeclaredAction(name, actionEnvironment, actionNode);
		});
	}
}
