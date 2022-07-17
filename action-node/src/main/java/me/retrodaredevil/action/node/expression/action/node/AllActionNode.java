package me.retrodaredevil.action.node.expression.action.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.BooleanExpression;
import me.retrodaredevil.action.node.expression.action.AllAction;

import static java.util.Objects.requireNonNull;

public class AllActionNode implements ActionNode {
	private final BooleanExpression expression;

	@JsonCreator
	public AllActionNode(@JsonProperty("expression") BooleanExpression expression) {
		requireNonNull(this.expression = expression);
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return new AllAction(expression);
	}
}
