package me.retrodaredevil.action.node;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.environment.ActionEnvironment;

@JsonTypeName("pass")
public class PassActionNode implements ActionNode {
	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return Actions.createRunOnce(() -> {});
	}
}
