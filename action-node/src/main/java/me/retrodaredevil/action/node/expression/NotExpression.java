package me.retrodaredevil.action.node.expression;

import me.retrodaredevil.action.node.expression.result.BooleanExpressionResult;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class NotExpression implements BooleanExpression {
	private final Expression expression;

	public NotExpression(Expression expression) {
		this.expression = requireNonNull(expression);
	}

	@Override
	public List<? extends BooleanExpressionResult> evaluate() {
		List<? extends BooleanExpressionResult> results = ExpressionConvert.convertTo(expression.evaluate(), BooleanExpressionResult.class);
		// TODO use unmodifiable list
		return results.stream()
				.map(BooleanExpressionResult::not)
				.collect(Collectors.toList());
	}
}
