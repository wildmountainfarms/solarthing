package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;

@JsonTypeName("pass")
public class PassActionNode implements ActionNode {
	@Override
	public Action createAction() {
		return Actions.createRunOnce(() -> {});
	}
}
