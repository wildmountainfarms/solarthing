package me.retrodaredevil.action.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.environment.ActionEnvironment;

import static java.util.Objects.requireNonNull;

@JsonTypeName("invert")
public class InvertActionNode implements ActionNode {
	private final ActionNode actionNode;

	@JsonCreator
	public InvertActionNode(@JsonProperty(value = "action", required = true) ActionNode actionNode) {
		this.actionNode = requireNonNull(actionNode);
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		Action action = actionNode.createAction(actionEnvironment);
		return new InvertAction(action);
	}
}
