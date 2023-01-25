package me.retrodaredevil.action.node.expression.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.Expression;
import me.retrodaredevil.action.node.expression.ExpressionConvert;
import me.retrodaredevil.action.node.expression.StringExpression;
import me.retrodaredevil.action.node.expression.result.StringExpressionResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

@JsonTypeName("join")
public class JoinStringExpressionNode implements ExpressionNode {
	private final ExpressionNode expressionNode;
	private final ExpressionNode separatorNode;

	@JsonCreator
	public JoinStringExpressionNode(
			@JsonProperty(value = "expression", required = true) ExpressionNode expressionNode,
			@JsonProperty("separator") ExpressionNode separatorNode
	) {
		this.expressionNode = requireNonNull(expressionNode);
		this.separatorNode = separatorNode == null
				? (_actionEnvironment) -> StringExpression.createConstant(Arrays.asList(new StringExpressionResult("")))
				: separatorNode;
	}

	@Override
	public Expression createExpression(ActionEnvironment actionEnvironment) {
		Expression expression = expressionNode.createExpression(actionEnvironment);
		Expression separator = separatorNode.createExpression(actionEnvironment);
		return () -> {
			StringBuilder r = new StringBuilder();
			boolean firstElement = true;
			List<StringExpressionResult> results = ExpressionConvert.convertTo(expression.evaluate(), StringExpressionResult.class);
			List<StringExpressionResult> separatorResults = ExpressionConvert.convertTo(separator.evaluate(), StringExpressionResult.class);
			for (StringExpressionResult result : results) {
				if (!firstElement) {
					for (StringExpressionResult separatorResult : separatorResults) {
						r.append(separatorResult.getString());
					}
				}
				r.append(result.getString());
				firstElement = false;
			}
			return Collections.singletonList(new StringExpressionResult(r.toString()));
		};
	}
}
