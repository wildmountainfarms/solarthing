package me.retrodaredevil.notation.antlr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import me.retrodaredevil.actions.lang.antlr.NotationScriptBaseVisitor;
import me.retrodaredevil.actions.lang.antlr.NotationScriptLexer;
import me.retrodaredevil.actions.lang.antlr.NotationScriptParser;
import me.retrodaredevil.notation.Argument;
import me.retrodaredevil.notation.ArrayArgument;
import me.retrodaredevil.notation.BooleanArgument;
import me.retrodaredevil.notation.Node;
import me.retrodaredevil.notation.NumberArgument;
import me.retrodaredevil.notation.StringArgument;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class NodeParser {
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private NodeParser() { throw new UnsupportedOperationException(); }

	public static Node parseFrom(CharStream charStream) throws SyntaxException {
		NotationScriptLexer lexer = new NotationScriptLexer(charStream);

		ParseErrorListener parseErrorListener = new ParseErrorListener();
		NotationScriptParser parser = new NotationScriptParser(new CommonTokenStream(lexer));
		parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
		parser.addErrorListener(parseErrorListener);

		// Parse the input and get the parse tree
		NotationScriptParser.NodeContext nodeContext = parser.node();
		if (!parseErrorListener.getSyntaxErrors().isEmpty()) {
			throw new SyntaxException(parseErrorListener.getSyntaxErrors());
		}

		Visitor visitor = new Visitor();
		return visitor.visitNode(nodeContext);
	}
	private static StringArgument stringArgumentFromString(TerminalNode stringTerminal) {
		return new StringArgument(stringFromString(stringTerminal));
	}

	private static String stringFromString(TerminalNode stringTerminal) {
		String rawText = stringTerminal.getText();
		final TextNode text;
		try {
			text = MAPPER.readValue(rawText, TextNode.class);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Incorrectly formatted string", e);
		}
		return text.textValue();
	}
	private static boolean booleanFromBoolean(TerminalNode booleanTerminal) {
		return booleanTerminal.getText().equals("true");
	}
	private static String stringFromLenientIdentifier(NotationScriptParser.Lenient_identifierContext lenientIdentifier) {
		if (lenientIdentifier.IDENTIFIER() != null) {
			return lenientIdentifier.IDENTIFIER().getText();
		}
		if (lenientIdentifier.STRING() != null) {
			return stringFromString(lenientIdentifier.STRING());
		}
		if (lenientIdentifier.BOOLEAN() != null) {
			return lenientIdentifier.BOOLEAN().getText();
		}
		if (lenientIdentifier.number() != null) {
			return lenientIdentifier.number().getText();
		}
		throw new AssertionError("Not a valid lenient identifier! (this should never happen) text: " + lenientIdentifier.getText());
	}

	private static class Visitor extends NotationScriptBaseVisitor<Argument> {
		@Override
		public Node visitNode(NotationScriptParser.NodeContext ctx) {
			NotationScriptParser.Node_partContext nodePart = ctx.node_part();
			String identifier = nodePart.IDENTIFIER().getText();
			List<Argument> arguments = new ArrayList<>();
			Map<String, Argument> namedArguments = new LinkedHashMap<>();
			if (nodePart.simple_argument() != null) {
				Argument argument = visitSimple_argument(nodePart.simple_argument());
				arguments.add(argument);
			} else {
				if (nodePart.argument_list() != null) {
					for (NotationScriptParser.ArgumentContext argumentCtx : nodePart.argument_list().argument()) {
						Argument argument = visitArgument(argumentCtx);
						arguments.add(argument);
					}
				}
				if (nodePart.named_argument_list() != null) {
					for (int i = 0; i < nodePart.named_argument_list().argument().size(); i++) {
						String argumentIdentifier = stringFromLenientIdentifier(nodePart.named_argument_list().lenient_identifier(i));
						NotationScriptParser.ArgumentContext argumentCtx = nodePart.named_argument_list().argument(i);
						Argument argument = visitArgument(argumentCtx);
						if (namedArguments.containsKey(argumentIdentifier)) {
							// TODO possibly throw a different exception here
							throw new IllegalArgumentException("Same argument key used more than once! identifier: " + argumentIdentifier);
						}
						namedArguments.put(argumentIdentifier, argument);
					}
				}
			}
			List<Node> subNodes = new ArrayList<>();
			if (ctx.node_list_part() != null) {
				NotationScriptParser.Node_list_partContext nodeList = ctx.node_list_part();
				for (NotationScriptParser.NodeContext node : nodeList.node()) {
					subNodes.add(visitNode(node));
				}
			}
			final Node linkedNode;
			if (ctx.linked_node_part() != null) {
				linkedNode = visitNode(ctx.linked_node_part().node());
			} else {
				linkedNode = null;
			}
			return new Node(identifier, arguments, namedArguments, subNodes, linkedNode);
		}

		@Override
		public Argument visitArgument(NotationScriptParser.ArgumentContext ctx) {
			if (ctx.node() != null) {
				return visitNode(ctx.node());
			}
			if (ctx.number() != null) {
				return visitNumber(ctx.number());
			}
			if (ctx.array() != null) {
				return visitArray(ctx.array());
			}
			if (ctx.STRING() != null) {
				return stringArgumentFromString(ctx.STRING());
			}
			if (ctx.BOOLEAN() != null) {
				return BooleanArgument.get(booleanFromBoolean(ctx.BOOLEAN()));
			}
			throw new UnsupportedOperationException("Unknown state of ArgumentContext! ctx: " + ctx.getText());
		}

		@Override
		public Argument visitSimple_argument(NotationScriptParser.Simple_argumentContext ctx) {
			if (ctx.number() != null) {
				return visitNumber(ctx.number());
			}
			if (ctx.array() != null) {
				return visitArray(ctx.array());
			}
			if (ctx.IDENTIFIER() != null) {
				return new StringArgument(ctx.IDENTIFIER().getText());
			}
			if (ctx.STRING() != null) {
				return stringArgumentFromString(ctx.STRING());
			}
			if (ctx.BOOLEAN() != null) {
				return BooleanArgument.get(ctx.BOOLEAN().getText().equals("true"));
			}
			throw new UnsupportedOperationException("Unknown state of Simple_argumentContext! ctx: " + ctx.getText());
		}

		@Override
		public NumberArgument visitNumber(NotationScriptParser.NumberContext ctx) {
			String numberText = ctx.NUMBER().getText();
			BigDecimal number = new BigDecimal(numberText);
			return new NumberArgument(number);
		}

		@Override
		public ArrayArgument visitArray(NotationScriptParser.ArrayContext ctx) {
			List<Argument> arguments = new ArrayList<>();
			for (NotationScriptParser.ArgumentContext argumentContext : ctx.argument()) {
				arguments.add(visitArgument(argumentContext));
			}
			return new ArrayArgument(arguments);
		}
	}
}
