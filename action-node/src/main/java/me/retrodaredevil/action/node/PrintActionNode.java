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
import me.retrodaredevil.action.node.expression.type.PrimitiveExpressionType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.PrintStream;
import java.util.List;

import static java.util.Objects.requireNonNull;

@JsonTypeName("print")
@NullMarked
public class PrintActionNode implements ActionNode {
	private final PrintStream printStream;
	private final @Nullable String message;
	private final @Nullable ExpressionNode expressionNode;

	@JsonCreator
	public PrintActionNode(
			@JsonProperty(value = "message") String message,
			@JsonProperty(value = "expression") @Nullable ExpressionNode expressionNode,
			@JsonProperty("stderr") @Nullable Boolean stderr
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
