package me.retrodaredevil.action.node.expression.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.BooleanExpression;
import me.retrodaredevil.action.node.expression.NumericExpression;
import me.retrodaredevil.action.node.expression.StringExpression;
import me.retrodaredevil.action.node.expression.TypedExpression;
import me.retrodaredevil.action.node.expression.result.BooleanExpressionResult;
import me.retrodaredevil.action.node.expression.result.NumericExpressionResult;
import me.retrodaredevil.action.node.expression.result.StringExpressionResult;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@JsonTypeName("const")
public class ConstantExpressionNode implements ExpressionNode {
	private final TypedExpression<?> expression;

	@JsonCreator
	public ConstantExpressionNode(
			@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
			@JsonProperty(value = "value", required = true) List<Object> list) {
		requireNonNull(list);
		if (list.isEmpty()) {
			throw new IllegalArgumentException("Cannot give empty list for const!");
		}
		Object firstElement = list.get(0);
		if (firstElement instanceof String) {
			expression = StringExpression.createConstant(
					list.stream()
							.map(element -> (String) element)
							.map(StringExpressionResult::new)
							.collect(Collectors.toList())
			);
		} else if (firstElement instanceof Number) {
			expression = NumericExpression.createConstant(
					list.stream()
							.map(element -> (Number) element)
							.map(NumericExpressionResult::create)
							.collect(Collectors.toList())
			);
		} else if (firstElement instanceof Boolean) {
			expression = BooleanExpression.createConstant(
					list.stream()
							.map(element -> (Boolean) element)
							.map(BooleanExpressionResult::get)
							.collect(Collectors.toList())
			);
		} else {
			throw new IllegalArgumentException("Unknown element type: " + firstElement.getClass().getName());
		}
	}

	@Override
	public TypedExpression<?> createExpression(ActionEnvironment actionEnvironment) {
		return expression;
	}
}
