package me.retrodaredevil.action.node.expression.action;

import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.action.node.expression.BooleanExpression;
import me.retrodaredevil.action.node.expression.result.BooleanExpressionResult;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class AnyAction extends SimpleAction {
	private final BooleanExpression expression;

	public AnyAction(BooleanExpression expression) {
		super(false);
		requireNonNull(this.expression = expression);
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		List<? extends BooleanExpressionResult> resultList = expression.evaluate();
		for (BooleanExpressionResult result : resultList) {
			if (result.getBoolean()) {
				setDone(true);
				return;
			}
		}
		setDone(false);
	}
}
