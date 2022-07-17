package me.retrodaredevil.action.node.expression.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.NumericExpression;
import me.retrodaredevil.action.node.expression.result.NumericExpressionResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@JsonTypeName("constant-number")
public class ConstantNumericExpressionNode implements NumericExpressionNode {
	private final List<BigDecimal> numberList;

	@JsonCreator
	public ConstantNumericExpressionNode(
			@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
			@JsonProperty(value = "constant", required = true) List<BigDecimal> numberList) {
		requireNonNull(this.numberList = numberList);
	}

	@Override
	public NumericExpression createExpression(ActionEnvironment actionEnvironment) {
		return () -> numberList.stream().map(NumericExpressionResult::create).collect(Collectors.toList());
	}
}
