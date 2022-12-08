package me.retrodaredevil.action.lang;

import me.retrodaredevil.actions.lang.antlr.ActionLangBaseVisitor;
import me.retrodaredevil.actions.lang.antlr.ActionLangLexer;
import me.retrodaredevil.actions.lang.antlr.ActionLangParser;
import me.retrodaredevil.actions.lang.antlr.ActionLangVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;

public class ActionLangTest {
	@Test
	void testCode() throws IOException {
		ActionLangLexer lexer = new ActionLangLexer(CharStreams.fromStream(
				requireNonNull(getClass().getResourceAsStream("/test_code1.txt"), "Expected file to be present!")
		));
		ActionLangParser parser = new ActionLangParser(new CommonTokenStream(lexer));

		// Parse the input and get the parse tree
		ActionLangParser.NodeContext nodeContext = parser.node();

		// Create a visitor to process the parse tree
		ActionLangVisitor<BigDecimal> visitor = new ActionLangBaseVisitor<BigDecimal>() {

		};
		System.out.println(nodeContext.toStringTree(parser));
	}

}
