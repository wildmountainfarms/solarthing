package me.retrodaredevil.action.node.expression.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.BooleanExpression;
import me.retrodaredevil.action.node.expression.ComparisonExpression;
import me.retrodaredevil.action.node.expression.NumericExpression;

import static java.util.Objects.requireNonNull;

public class ComparisonExpressionNode implements BooleanExpressionNode {
	private final NumericExpression lhs;
	private final NumericExpression rhs;
	private final ComparisonExpression.Operator operator;

	@JsonCreator
	public ComparisonExpressionNode(
			@JsonProperty(value = "lhs", required = true) NumericExpression lhs,
			@JsonProperty(value = "rhs", required = true) NumericExpression rhs,
			@JsonProperty(value = "operator", required = true) ComparisonExpression.Operator operator) {
		requireNonNull(this.lhs = lhs);
		requireNonNull(this.rhs = rhs);
		requireNonNull(this.operator = operator);
	}

	@Override
	public BooleanExpression createExpression(ActionEnvironment actionEnvironment) {
		return new ComparisonExpression(lhs, rhs, operator);
	}
}
