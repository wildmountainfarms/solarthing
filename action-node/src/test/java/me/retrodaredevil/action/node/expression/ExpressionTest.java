package me.retrodaredevil.action.node.expression;

import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.expression.action.AllAction;
import me.retrodaredevil.action.node.expression.action.AnyAction;
import me.retrodaredevil.action.node.expression.result.BooleanExpressionResult;
import me.retrodaredevil.action.node.expression.result.NumericExpressionResult;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionTest {

	private static boolean evaluateSingleBoolean(BooleanExpression expression) {
		List<? extends BooleanExpressionResult> result = expression.evaluate();
		assertEquals(1, result.size());
		return result.get(0).getBoolean();
	}
	private static ComparisonExpression createComparisonExpression(Number lhs, Number rhs, ComparisonExpression.Operator operator) {
		return new ComparisonExpression(
				() -> Collections.singletonList(NumericExpressionResult.create(lhs)),
				() -> Collections.singletonList(NumericExpressionResult.create(rhs)),
				operator
		);
	}

	@Test
	void test() {
		assertFalse(evaluateSingleBoolean(createComparisonExpression(1.0, 1.0, ComparisonExpression.Operator.GREATER_THAN)));
		assertFalse(evaluateSingleBoolean(createComparisonExpression(1.0, 1.0, ComparisonExpression.Operator.LESS_THAN)));
		assertTrue(evaluateSingleBoolean(createComparisonExpression(1.0, 1.0, ComparisonExpression.Operator.GREATER_THAN_OR_EQUAL)));
		assertTrue(evaluateSingleBoolean(createComparisonExpression(1.0, 1.0, ComparisonExpression.Operator.LESS_THAN_OR_EQUAL)));

		assertTrue(evaluateSingleBoolean(createComparisonExpression(1.1, 1.0, ComparisonExpression.Operator.GREATER_THAN)));
		assertFalse(evaluateSingleBoolean(createComparisonExpression(1.1, 1.0, ComparisonExpression.Operator.LESS_THAN)));
		assertTrue(evaluateSingleBoolean(createComparisonExpression(1.1, 1.0, ComparisonExpression.Operator.GREATER_THAN_OR_EQUAL)));
		assertFalse(evaluateSingleBoolean(createComparisonExpression(1.1, 1.0, ComparisonExpression.Operator.LESS_THAN_OR_EQUAL)));
	}

	@Test
	void testAllAction() {
		@SuppressWarnings("unchecked")
		List<BooleanExpressionResult>[] currentResult = new List[] { null };
		currentResult[0] = Arrays.asList(BooleanExpressionResult.FALSE, BooleanExpressionResult.TRUE, BooleanExpressionResult.TRUE);
		BooleanExpression expression = () -> currentResult[0];
		Action allAction = new AllAction(expression);

		allAction.update();
		assertFalse(allAction.isDone());
		allAction.update();
		assertFalse(allAction.isDone());

		currentResult[0] = Arrays.asList(BooleanExpressionResult.TRUE, BooleanExpressionResult.TRUE, BooleanExpressionResult.TRUE);
		allAction.update();
		assertTrue(allAction.isDone());
	}

	@Test
	void testAnyAction() {
		@SuppressWarnings("unchecked")
		List<BooleanExpressionResult>[] currentResult = new List[] { null };
		currentResult[0] = Arrays.asList(BooleanExpressionResult.FALSE, BooleanExpressionResult.FALSE, BooleanExpressionResult.FALSE);
		BooleanExpression expression = () -> currentResult[0];
		Action anyAction = new AnyAction(expression);

		anyAction.update();
		assertFalse(anyAction.isDone());
		anyAction.update();
		assertFalse(anyAction.isDone());

		currentResult[0] = Arrays.asList(BooleanExpressionResult.TRUE, BooleanExpressionResult.FALSE, BooleanExpressionResult.FALSE);
		anyAction.update();
		assertTrue(anyAction.isDone());
	}
}
