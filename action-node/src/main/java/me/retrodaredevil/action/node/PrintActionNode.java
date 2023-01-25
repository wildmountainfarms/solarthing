package me.retrodaredevil.action.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.Expression;
import me.retrodaredevil.action.node.expression.ExpressionConvert;
import me.retrodaredevil.action.node.expression.node.ExpressionNode;
import me.retrodaredevil.action.node.expression.result.StringExpressionResult;
import me.retrodaredevil.action.node.expression.type.ExpressionType;
import me.retrodaredevil.action.node.expression.type.PrimitiveExpressionType;

import java.io.PrintStream;
import java.util.List;

import static java.util.Objects.requireNonNull;

@JsonTypeName("print")
public class PrintActionNode implements ActionNode {
	private final PrintStream printStream;
	private final String message;
	private final ExpressionNode expressionNode;

	@JsonCreator
	public PrintActionNode(
			@JsonProperty(value = "message") String message,
			@JsonProperty(value = "expression") ExpressionNode expressionNode,
			@JsonProperty("stderr") Boolean stderr
	) {
		if (expressionNode == null) {
			this.message = requireNonNull(message);
			this.expressionNode = null;
		} else {
			this.message = null;
			this.expressionNode = expressionNode;
		}
		this.printStream = Boolean.TRUE.equals(stderr) ? System.err : System.out;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		Expression expression = expressionNode == null ? null : expressionNode.createExpression(actionEnvironment);
		if (expression != null) {
			expression.checkSupport(PrimitiveExpressionType.STRING);
		}
		return Actions.createRunOnce(() -> {
			final String toPrint;
			if (message != null) {
				toPrint = message;
			} else {
				requireNonNull(expression, "This should never happen");
				List<StringExpressionResult> resultList = ExpressionConvert.convertTo(expression.evaluate(), StringExpressionResult.class);
				if (resultList.size() != 1) {
					throw new IllegalStateException("Unsupported result list size! size: " + resultList.size());
				}
				toPrint = resultList.get(0).getString();
			}
			printStream.println(toPrint);
		});
	}
}
