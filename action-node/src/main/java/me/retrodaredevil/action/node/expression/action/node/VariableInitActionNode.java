package me.retrodaredevil.action.node.expression.action.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;
import me.retrodaredevil.action.node.expression.Expression;
import me.retrodaredevil.action.node.expression.node.ExpressionNode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@JsonTypeName("init")
@NullMarked
public class VariableInitActionNode implements ActionNode {
	private final String variableName;
	private final @Nullable ExpressionNode expressionNode;

	@JsonCreator
	public VariableInitActionNode(
			@JsonProperty(value = "name", required = true) String variableName,
			@JsonProperty(value = "expression") @Nullable ExpressionNode expressionNode) {
		this.variableName = requireNonNull(variableName);
		this.expressionNode = expressionNode;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		VariableEnvironment variableEnvironment = actionEnvironment.getVariableEnvironment();
		Expression expression = expressionNode == null ? null : expressionNode.createExpression(actionEnvironment);
		return Actions.createRunOnce(() -> {
			variableEnvironment.initializeVariable(variableName, expression == null ? null : expression.evaluateToConstant());
		});
	}
}
