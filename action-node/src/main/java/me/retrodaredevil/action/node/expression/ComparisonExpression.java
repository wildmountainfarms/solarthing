package me.retrodaredevil.action.node.expression;

import com.fasterxml.jackson.annotation.JsonValue;
import me.retrodaredevil.action.node.expression.result.BooleanExpressionResult;
import me.retrodaredevil.action.node.expression.result.NumericExpressionResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Essentially this is a numeric binary expression, but is currently only used upon comparisons between numeric types.
 */
public class ComparisonExpression implements BooleanExpression {
	private final NumericExpression lhs;
	private final NumericExpression rhs;
	private final Operator operator;

	public ComparisonExpression(NumericExpression lhs, NumericExpression rhs, Operator operator) {
		requireNonNull(this.lhs = lhs);
		requireNonNull(this.rhs = rhs);
		requireNonNull(this.operator = operator);
	}

	public enum Operator {
		GREATER_THAN(">") {
			@Override
			public <T extends Comparable<T>> boolean evaluate(T lhs, T rhs) {
				return lhs.compareTo(rhs) > 0;
			}
		},
		LESS_THAN("<") {
			@Override
			public <T extends Comparable<T>> boolean evaluate(T lhs, T rhs) {
				return lhs.compareTo(rhs) < 0;
			}
		},
		GREATER_THAN_OR_EQUAL(">=") {
			@Override
			public <T extends Comparable<T>> boolean evaluate(T lhs, T rhs) {
				return lhs.compareTo(rhs) >= 0;
			}
		},
		LESS_THAN_OR_EQUAL("<=") {
			@Override
			public <T extends Comparable<T>> boolean evaluate(T lhs, T rhs) {
				return lhs.compareTo(rhs) <= 0;
			}
		},
		;
		@JsonValue
		private final String sign;

		Operator(String sign) {
			this.sign = sign;
		}
		public static Operator fromSignOrNull(String sign) {
			requireNonNull(sign);
			return Arrays.stream(values()).filter(operator -> operator.sign.equals(sign)).findAny().orElse(null);
		}

		public abstract <T extends Comparable<T>> boolean evaluate(T lhs, T rhs);
	}
	@Override
	public List<? extends BooleanExpressionResult> evaluate() {
		List<? extends NumericExpressionResult> leftResultList = lhs.evaluate();
		List<? extends NumericExpressionResult> rightResultList = rhs.evaluate();
		List<BooleanExpressionResult> resultList = new ArrayList<>();
		for (NumericExpressionResult leftResult : leftResultList) {
			for (NumericExpressionResult rightResult : rightResultList) {
				resultList.add(BooleanExpressionResult.get(operator.evaluate(leftResult, rightResult)));
			}
		}
		return resultList;
	}
}
