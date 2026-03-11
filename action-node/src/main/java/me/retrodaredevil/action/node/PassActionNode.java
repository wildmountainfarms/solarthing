package me.retrodaredevil.action.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import org.jspecify.annotations.NullMarked;

@JsonTypeName("pass")
@NullMarked
public class PassActionNode implements ActionNode {
	private static final PassActionNode INSTANCE = new PassActionNode();
	@JsonCreator
	public static PassActionNode getInstance() {
		return INSTANCE;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return Actions.createRunOnce(() -> {});
	}
}
