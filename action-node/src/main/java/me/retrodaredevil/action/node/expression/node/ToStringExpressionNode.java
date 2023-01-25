package me.retrodaredevil.action.node.expression.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.Expression;
import me.retrodaredevil.action.node.expression.result.BooleanExpressionResult;
import me.retrodaredevil.action.node.expression.result.NumericExpressionResult;
import me.retrodaredevil.action.node.expression.result.StringExpressionResult;

import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@JsonTypeName("str")
public class ToStringExpressionNode implements ExpressionNode {
	private final ExpressionNode expressionNode;

	@JsonCreator
	public ToStringExpressionNode(@JsonProperty(value = "expression", required = true) ExpressionNode expressionNode) {
		this.expressionNode = requireNonNull(expressionNode);
	}

	@Override
	public Expression createExpression(ActionEnvironment actionEnvironment) {
		Expression expression = expressionNode.createExpression(actionEnvironment);
		return () -> expression.evaluate().stream()
				.map(expressionResult -> {
					if (expressionResult instanceof StringExpressionResult) {
						return (StringExpressionResult) expressionResult;
					} else if (expressionResult instanceof NumericExpressionResult) {
						Number number = ((NumericExpressionResult) expressionResult).getNumber();
						return new StringExpressionResult(number.toString());
					} else if (expressionResult instanceof BooleanExpressionResult) {
						boolean value = ((BooleanExpressionResult) expressionResult).getBoolean();
						return new StringExpressionResult(value ? "true" : "false");
					} else {
						throw new UnsupportedOperationException("Unsupported expression result to convert to string. expression result: " + expressionResult);
					}
				})
				.collect(Collectors.toList());
	}
}
