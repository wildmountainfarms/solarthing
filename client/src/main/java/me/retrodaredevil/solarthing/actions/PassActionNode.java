package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;

@JsonTypeName("pass")
public class PassActionNode implements ActionNode {
	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return Actions.createRunOnce(() -> {});
	}
}
