package me.retrodaredevil.action.node.scope;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;

import static java.util.Objects.requireNonNull;

@JsonTypeName("scope")
public class ScopeActionNode implements ActionNode {

	private final ActionNode actionNode;

	@JsonCreator
	public ScopeActionNode(@JsonProperty(value = "action", required = true) ActionNode actionNode) {
		this.actionNode = requireNonNull(actionNode);
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		VariableEnvironment outerVariableEnvironment = actionEnvironment.getVariableEnvironment();
		VariableEnvironment variableEnvironment = new VariableEnvironment(outerVariableEnvironment);
		ActionEnvironment innerActionEnvironment = new ActionEnvironment(variableEnvironment, actionEnvironment.getInjectEnvironment());
		return actionNode.createAction(innerActionEnvironment);
	}
}
