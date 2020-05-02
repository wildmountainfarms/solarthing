package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;

import java.util.ArrayList;
import java.util.List;

@JsonTypeName("queue")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public class QueueActionNode implements ActionNode {
	private final List<ActionNode> actionNodes;

	@JsonCreator
	public QueueActionNode(@JsonProperty("actions") List<ActionNode> actionNodes) {
		this.actionNodes = actionNodes;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		List<Action> actions = new ArrayList<>();
		for (ActionNode actionNode : actionNodes) {
			actions.add(actionNode.createAction(actionEnvironment));
		}
		return new Actions.ActionQueueBuilder(actions.toArray(new Action[0]))
				.immediatelyDoNextWhenDone(true)
				.build();
	}
}
