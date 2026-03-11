package me.retrodaredevil.action.node.expression.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.Expression;
import me.retrodaredevil.action.node.expression.NotExpression;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@JsonTypeName("not")
@NullMarked
public class NotExpressionNode implements ExpressionNode {
	private final ExpressionNode expressionNode;

	@JsonCreator
	public NotExpressionNode(
			@JsonProperty(value = "expression", required = true) ExpressionNode expressionNode) {
		this.expressionNode = requireNonNull(expressionNode);
	}

	@Override
	public Expression createExpression(ActionEnvironment actionEnvironment) {
		return new NotExpression(expressionNode.createExpression(actionEnvironment));
	}
}
