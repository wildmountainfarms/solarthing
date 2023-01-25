package me.retrodaredevil.action.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.environment.ActionEnvironment;

import java.util.List;

import static java.util.Objects.requireNonNull;

@JsonTypeName("parallel")
public class ParallelActionNode implements ActionNode {
	private final List<ActionNode> actionNodes;

	@JsonCreator
	public ParallelActionNode(@JsonProperty(value = "actions", required = true) List<ActionNode> actionNodes) {
		this.actionNodes = requireNonNull(actionNodes);
	}
	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return new Actions.ActionMultiplexerBuilder(
				actionNodes.stream()
						.map(actionNode -> actionNode.createAction(actionEnvironment))
						.toArray(Action[]::new)
		)
				.build();

	}
}
