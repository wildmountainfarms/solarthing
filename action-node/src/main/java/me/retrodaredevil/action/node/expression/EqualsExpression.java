package me.retrodaredevil.action.node.expression;

import me.retrodaredevil.action.node.expression.result.BooleanExpressionResult;
import me.retrodaredevil.action.node.expression.result.ExpressionResult;

import java.util.ArrayList;
import java.util.List;

public class EqualsExpression implements BooleanExpression {
	private final Expression lhs;
	private final Expression rhs;

	public EqualsExpression(Expression lhs, Expression rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public List<? extends BooleanExpressionResult> evaluate() {
		List<? extends ExpressionResult> leftResultList = lhs.evaluate();
		List<? extends ExpressionResult> rightResultList = rhs.evaluate();
		List<BooleanExpressionResult> resultList = new ArrayList<>();
		for (ExpressionResult leftResult : leftResultList) {
			for (ExpressionResult rightResult : rightResultList) {
				resultList.add(BooleanExpressionResult.get(leftResult.equals(rightResult)));
			}
		}
		return resultList;
	}
}
