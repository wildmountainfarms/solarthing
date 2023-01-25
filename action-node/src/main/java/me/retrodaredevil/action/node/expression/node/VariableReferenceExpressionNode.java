package me.retrodaredevil.action.node.expression.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;
import me.retrodaredevil.action.node.expression.Expression;

@JsonTypeName("ref")
public class VariableReferenceExpressionNode implements ExpressionNode {
	private final String variableName;

	@JsonCreator
	public VariableReferenceExpressionNode(@JsonProperty(value = "name", required = true) String variableName) {
		this.variableName = variableName;
	}

	@Override
	public Expression createExpression(ActionEnvironment actionEnvironment) {
		VariableEnvironment variableEnvironment = actionEnvironment.getVariableEnvironment();
		return () -> variableEnvironment.referenceVariable(variableName).evaluate();
	}
}
