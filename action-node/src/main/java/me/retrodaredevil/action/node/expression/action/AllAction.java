package me.retrodaredevil.action.node.expression.action;

import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.action.node.expression.Expression;
import me.retrodaredevil.action.node.expression.ExpressionConvert;
import me.retrodaredevil.action.node.expression.result.BooleanExpressionResult;
import me.retrodaredevil.action.node.expression.type.PrimitiveExpressionType;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class AllAction extends SimpleAction {
	private final Expression expression;
	private final boolean any;

	public AllAction(Expression expression, boolean any) {
		super(false);
		this.expression = requireNonNull(expression);
		this.any = any;
		expression.checkSupport(PrimitiveExpressionType.BOOLEAN);
	}
	private boolean areAll(boolean value) {
		List<BooleanExpressionResult> resultList = ExpressionConvert.convertTo(expression.evaluate(), BooleanExpressionResult.class);
		for (BooleanExpressionResult result : resultList) {
			if (result.getBoolean() != value) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		setDone(areAll(!any) != any);
	}
}
