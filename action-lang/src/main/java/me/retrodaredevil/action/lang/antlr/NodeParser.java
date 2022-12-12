package me.retrodaredevil.action.lang.antlr;

import me.retrodaredevil.action.lang.Argument;
import me.retrodaredevil.action.lang.ArrayArgument;
import me.retrodaredevil.action.lang.BooleanArgument;
import me.retrodaredevil.action.lang.Node;
import me.retrodaredevil.action.lang.NumberArgument;
import me.retrodaredevil.action.lang.StringArgument;
import me.retrodaredevil.actions.lang.antlr.ActionLangBaseVisitor;
import me.retrodaredevil.actions.lang.antlr.ActionLangLexer;
import me.retrodaredevil.actions.lang.antlr.ActionLangParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class NodeParser {
	private NodeParser() { throw new UnsupportedOperationException(); }

	public static Node parseFrom(CharStream charStream) {
		ActionLangLexer lexer = new ActionLangLexer(charStream);
		ActionLangParser parser = new ActionLangParser(new CommonTokenStream(lexer));

		// Parse the input and get the parse tree
		ActionLangParser.NodeContext nodeContext = parser.node();

		Visitor visitor = new Visitor();
		return visitor.visitNode(nodeContext);
	}

	private static class Visitor extends ActionLangBaseVisitor<Argument> {
		@Override
		public Node visitNode(ActionLangParser.NodeContext ctx) {
			ActionLangParser.Node_partContext nodePart = ctx.node_part();
			String identifier = nodePart.IDENTIFIER().getText();
			List<Argument> arguments = new ArrayList<>();
			Map<String, Argument> namedArguments = new LinkedHashMap<>();
			if (nodePart.simple_argument() != null) {
				Argument argument = visitSimple_argument(nodePart.simple_argument());
				arguments.add(argument);
			} else {
				if (nodePart.argument_list() != null) {
					for (ActionLangParser.ArgumentContext argumentCtx : nodePart.argument_list().argument()) {
						Argument argument = visitArgument(argumentCtx);
						arguments.add(argument);
					}
				}
				if (nodePart.named_argument_list() != null) {
					for (int i = 0; i < nodePart.named_argument_list().argument().size(); i++) {
						TerminalNode identifierTerminal = nodePart.named_argument_list().IDENTIFIER(i);
						ActionLangParser.ArgumentContext argumentCtx = nodePart.named_argument_list().argument(i);
						String argumentIdentifier = identifierTerminal.getText();
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
				ActionLangParser.Node_list_partContext nodeList = ctx.node_list_part();
				for (ActionLangParser.NodeContext node : nodeList.node()) {
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
		public Argument visitArgument(ActionLangParser.ArgumentContext ctx) {
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
				return new StringArgument(ctx.STRING().getText());
			}
			if (ctx.BOOLEAN() != null) {
				return BooleanArgument.get(ctx.BOOLEAN().getText().equals("true"));
			}
			throw new UnsupportedOperationException("Unknown state of ArgumentContext! ctx: " + ctx.getText());
		}

		@Override
		public Argument visitSimple_argument(ActionLangParser.Simple_argumentContext ctx) {
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
				return new StringArgument(ctx.STRING().getText());
			}
			if (ctx.BOOLEAN() != null) {
				return BooleanArgument.get(ctx.BOOLEAN().getText().equals("true"));
			}
			throw new UnsupportedOperationException("Unknown state of Simple_argumentContext! ctx: " + ctx.getText());
		}

		@Override
		public NumberArgument visitNumber(ActionLangParser.NumberContext ctx) {
			String numberText = ctx.NUMBER().getText();
			BigDecimal number = new BigDecimal(numberText);
			return new NumberArgument(number);
		}

		@Override
		public ArrayArgument visitArray(ActionLangParser.ArrayContext ctx) {
			List<Argument> arguments = new ArrayList<>();
			for (ActionLangParser.ArgumentContext argumentContext : ctx.argument()) {
				arguments.add(visitArgument(argumentContext));
			}
			return new ArrayArgument(arguments);
		}
	}
}