package me.retrodaredevil.action.node.expression;

import me.retrodaredevil.action.node.expression.result.ExpressionResult;

import java.util.List;
import java.util.stream.Collectors;

public final class ExpressionConvert {
	private ExpressionConvert() { throw new UnsupportedOperationException(); }

	public static <T extends ExpressionResult> List<T> convertTo(List<? extends ExpressionResult> resultList, Class<T> to) {
		return resultList.stream()
				.map(expressionResult -> {
					try {
						return to.cast(expressionResult);
					} catch (ClassCastException e) {
						throw new IllegalArgumentException("Could not cast to: " + to, e);
					}
				})
				// TODO use unmodifiable list
				.collect(Collectors.toList());
	}
}
