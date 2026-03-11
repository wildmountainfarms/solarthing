package me.retrodaredevil.action.node.expression.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.BooleanExpression;
import me.retrodaredevil.action.node.expression.ComparisonExpression;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@JsonTypeName("comparison")
@NullMarked
public class ComparisonExpressionNode implements ExpressionNode {
	private final ExpressionNode lhs;
	private final ExpressionNode rhs;
	private final ComparisonExpression.Operator operator;

	@JsonCreator
	public ComparisonExpressionNode(
			@JsonProperty(value = "lhs", required = true) ExpressionNode lhs,
			@JsonProperty(value = "rhs", required = true) ExpressionNode rhs,
			@JsonProperty(value = "operator", required = true) ComparisonExpression.Operator operator) {
		this.lhs = requireNonNull(lhs);
		this.rhs = requireNonNull(rhs);
		this.operator = requireNonNull(operator);
	}

	@Override
	public BooleanExpression createExpression(ActionEnvironment actionEnvironment) {
		return new ComparisonExpression(
				lhs.createExpression(actionEnvironment),
				rhs.createExpression(actionEnvironment),
				operator
		);
	}
}
