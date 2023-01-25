package me.retrodaredevil.action.node.expression.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.Expression;
import me.retrodaredevil.action.node.expression.result.ExpressionResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@JsonTypeName("union")
public class UnionExpressionNode implements ExpressionNode {
	private final List<ExpressionNode> expressionNodes;

	@JsonCreator
	public UnionExpressionNode(@JsonProperty(value = "expressions", required = true) List<ExpressionNode> expressionNodes) {
		this.expressionNodes = requireNonNull(expressionNodes);
	}

	@Override
	public Expression createExpression(ActionEnvironment actionEnvironment) {
		List<Expression> expressions = expressionNodes.stream()
				.map(expressionNode -> expressionNode.createExpression(actionEnvironment))
				.collect(Collectors.toList());
		// TODO determine if we want to implement getType() at all
		// Also determine if we care that this makes it possible to combine different types into a single expression result list
		return () -> {
			List<ExpressionResult> r = new ArrayList<>();
			for (Expression expression : expressions) {
				r.addAll(expression.evaluate());
			}
			return r;
		};
	}
}
