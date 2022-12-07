package me.retrodaredevil.action.lang;

import me.retrodaredevil.actions.lang.antlr.ArithmeticBaseVisitor;
import me.retrodaredevil.actions.lang.antlr.ArithmeticLexer;
import me.retrodaredevil.actions.lang.antlr.ArithmeticParser;
import me.retrodaredevil.actions.lang.antlr.ArithmeticVisitor;
import org.antlr.v4.runtime.CodePointBuffer;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.CharBuffer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArithmeticAntlrTest {
	@Test
	void test() {
		ArithmeticLexer lexer = new ArithmeticLexer(CodePointCharStream.fromBuffer(
				CodePointBuffer.withChars(CharBuffer.wrap("1+2*3".toCharArray())) // CodePointBuffer requires that CharBuffer#array() is available
		));
		ArithmeticParser parser = new ArithmeticParser(new CommonTokenStream(lexer));

		// Parse the input and get the parse tree
		ParseTree tree = parser.expression();

		// Create a visitor to process the parse tree
		ArithmeticVisitor<BigDecimal> visitor = new ArithmeticBaseVisitor<BigDecimal>() {
			@Override
			public BigDecimal visitExpression(ArithmeticParser.ExpressionContext ctx) {
				List<ArithmeticParser.TermContext> terms = ctx.term();
				List<TerminalNode> operators = ctx.EXPRESSION_OP();
				BigDecimal result = visitTerm(terms.get(0));
				for (int i = 0; i < operators.size(); i++) {
					TerminalNode operator = operators.get(i);
					ArithmeticParser.TermContext term = terms.get(i + 1);
					BigDecimal evaluated = visitTerm(term);
					if ("+".equals(operator.getSymbol().getText())) {
						result = result.add(evaluated);
					} else if ("-".equals(operator.getSymbol().getText())) {
						result = result.subtract(evaluated);
					} else throw new AssertionError("Unknown symbol: " + operator.getSymbol().getText());
				}
				return result;
			}

			@Override
			public BigDecimal visitTerm(ArithmeticParser.TermContext ctx) {
				List<ArithmeticParser.FactorContext> factors = ctx.factor();
				List<TerminalNode> operators = ctx.TERM_OP();
				BigDecimal result = visitFactor(factors.get(0));
				for (int i = 0; i < operators.size(); i++) {
					TerminalNode operator = operators.get(i);
					ArithmeticParser.FactorContext factor = factors.get(i + 1);
					BigDecimal evaluated = visitFactor(factor);
					if ("*".equals(operator.getSymbol().getText())) {
						result = result.multiply(evaluated);
					} else if ("/".equals(operator.getSymbol().getText())) {
						result = result.divide(evaluated, RoundingMode.DOWN);
					} else throw new AssertionError("Unknown symbol: " + operator.getSymbol().getText());
				}
				return result;
			}

			@Override
			public BigDecimal visitFactor(ArithmeticParser.FactorContext ctx) {
				ArithmeticParser.ExpressionContext expressionContext = ctx.expression();
				if (expressionContext != null) {
					return visitExpression(expressionContext);
				}
				return new BigDecimal(ctx.getText());
			}
		};
		BigDecimal result = visitor.visit(tree);
		assertEquals(new BigDecimal(7), result);
	}
}
