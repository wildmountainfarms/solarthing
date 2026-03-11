package me.retrodaredevil.action.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@JsonTypeName("call")
@NullMarked
public class CallActionNode implements ActionNode {
	private final String name;
	public CallActionNode(@JsonProperty("name") String name) {
		this.name = requireNonNull(name);
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		VariableEnvironment variableEnvironment = actionEnvironment.getVariableEnvironment();
		return Actions.createDynamicActionRunner(() -> variableEnvironment.getDeclaredAction(name).createActionWithOriginalScope());
	}
}
