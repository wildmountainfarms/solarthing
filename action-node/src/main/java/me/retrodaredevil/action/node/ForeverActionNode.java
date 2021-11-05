package me.retrodaredevil.action.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.environment.ActionEnvironment;

@JsonTypeName("forever")
public class ForeverActionNode implements ActionNode {
	private static final ForeverActionNode INSTANCE = new ForeverActionNode();

	private ForeverActionNode() {}

	@JsonCreator
	public static ForeverActionNode getInstance() {
		return INSTANCE;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return Actions.createRunForever(() -> {});
	}
}
