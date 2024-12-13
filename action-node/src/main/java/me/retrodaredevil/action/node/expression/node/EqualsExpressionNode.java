package me.retrodaredevil.action.node.expression.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.EqualsExpression;
import me.retrodaredevil.action.node.expression.Expression;

import static java.util.Objects.requireNonNull;

@JsonTypeName("eq")
public class EqualsExpressionNode implements ExpressionNode {
	private final ExpressionNode lhs;
	private final ExpressionNode rhs;

	@JsonCreator
	public EqualsExpressionNode(
			@JsonProperty(value = "lhs", required = true) ExpressionNode lhs,
			@JsonProperty(value = "rhs", required = true) ExpressionNode rhs) {
		this.lhs = requireNonNull(lhs);
		this.rhs = requireNonNull(rhs);
	}

	@Override
	public Expression createExpression(ActionEnvironment actionEnvironment) {
		return new EqualsExpression(
				lhs.createExpression(actionEnvironment),
				rhs.createExpression(actionEnvironment)
		);
	}
}
