package me.retrodaredevil.action.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;
import me.retrodaredevil.action.node.expression.Expression;
import me.retrodaredevil.action.node.expression.node.ExpressionNode;

import static java.util.Objects.requireNonNull;

@JsonTypeName("init")
public class InitVariableActionNode implements ActionNode {
	private final String variableName;
	private final ExpressionNode expressionNode;

	@JsonCreator
	public InitVariableActionNode(
			@JsonProperty(value = "name", required = true) String variableName,
			@JsonProperty("expression") ExpressionNode expressionNode) {
		this.variableName = requireNonNull(variableName);
		this.expressionNode = expressionNode;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		VariableEnvironment variableEnvironment = actionEnvironment.getVariableEnvironment();
		Expression expression = expressionNode.createExpression(actionEnvironment);
		return Actions.createRunOnce(() -> {
			variableEnvironment.initializeVariable(variableName, expression);
		});
	}
}
