package me.retrodaredevil.action.node.expression.node;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.Expression;

@JsonSubTypes({
		@JsonSubTypes.Type(ComparisonExpressionNode.class),
		@JsonSubTypes.Type(ConstantNumericExpressionNode.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface ExpressionNode {
	Expression createExpression(ActionEnvironment actionEnvironment);
}
