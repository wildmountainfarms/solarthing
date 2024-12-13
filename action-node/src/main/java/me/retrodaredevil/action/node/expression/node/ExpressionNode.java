package me.retrodaredevil.action.node.expression.node;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.Expression;

@JsonSubTypes({
		@JsonSubTypes.Type(ComparisonExpressionNode.class),
		@JsonSubTypes.Type(EqualsExpressionNode.class),
		@JsonSubTypes.Type(NotExpressionNode.class),
		@JsonSubTypes.Type(ConstantExpressionNode.class),
		@JsonSubTypes.Type(VariableReferenceExpressionNode.class),
		@JsonSubTypes.Type(ToStringExpressionNode.class),
		@JsonSubTypes.Type(JoinStringExpressionNode.class),
		@JsonSubTypes.Type(ConcatExpressionNode.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface ExpressionNode {
	Expression createExpression(ActionEnvironment actionEnvironment);
}
