package me.retrodaredevil.action.node.expression.action.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.action.AnyAction;
import me.retrodaredevil.action.node.expression.node.BooleanExpressionNode;

import static java.util.Objects.requireNonNull;

@JsonTypeName("any")
public class AnyActionNode implements ActionNode {
	private final BooleanExpressionNode expression;

	@JsonCreator
	public AnyActionNode(@JsonProperty("expression") BooleanExpressionNode expression) {
		requireNonNull(this.expression = expression);
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return new AnyAction(expression.createExpression(actionEnvironment));
	}
}
