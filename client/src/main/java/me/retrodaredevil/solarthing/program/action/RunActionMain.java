package me.retrodaredevil.solarthing.program.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.lang.ActionLangUtil;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.InjectEnvironment;
import me.retrodaredevil.action.node.environment.NanoTimeProviderEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;
import me.retrodaredevil.action.node.util.NanoTimeProvider;
import me.retrodaredevil.notation.Node;
import me.retrodaredevil.notation.NodeTranslator;
import me.retrodaredevil.notation.antlr.NodeParser;
import me.retrodaredevil.notation.antlr.SyntaxError;
import me.retrodaredevil.notation.antlr.SyntaxException;
import me.retrodaredevil.notation.translators.json.JsonNodeTranslator;
import me.retrodaredevil.notation.translators.json.SimpleConfigurationProvider;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import org.antlr.v4.runtime.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@UtilityClass
public class RunActionMain {
	private RunActionMain() { throw new UnsupportedOperationException(); }

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final String USAGE = "" +
			"Usage: solarthing action [options] <file>\n" +
			"  --check                Set to only parse the program and exit\n" +
			"  --json                 Set to parse and output json of program";

	private static InputStream inputStreamFrom(String argument) throws IOException {
		if ("-".equals(argument)) {
			return System.in;
		}
		Path path = Paths.get(argument);
		return Files.newInputStream(path);
	}

	public static int runAction(String[] args) {
		Cli<RunActionOptions> cli = CliFactory.createCli(RunActionOptions.class);
		final RunActionOptions options;
		try {
			options = cli.parseArguments(args);
		} catch (ArgumentValidationException ex) {
			System.err.println(USAGE);
			if (ex instanceof HelpRequestedException) {
				return 0;
			}
			return SolarThingConstants.EXIT_CODE_INVALID_OPTIONS;
		}
		List<String> positionalArguments = options.getPositionalArguments();
		if (positionalArguments == null || positionalArguments.size() != 1) {
			System.err.println(USAGE);
			return SolarThingConstants.EXIT_CODE_INVALID_OPTIONS;
		}
		String inputArgument = positionalArguments.get(0);
		final Node node;
		try {
			InputStream inputStream = inputStreamFrom(inputArgument);
			node = NodeParser.parseFrom(CharStreams.fromStream(inputStream, StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("\nGot error while reading file: " + inputArgument);
			return SolarThingConstants.EXIT_CODE_FAIL;
		} catch (SyntaxException e) {
			for (SyntaxError error : e.getSyntaxErrors()) {
				System.err.println(error);
			}
			return SolarThingConstants.EXIT_CODE_FAIL;
		}
		NodeTranslator<JsonNode> translator = new JsonNodeTranslator(new SimpleConfigurationProvider(
				ActionLangUtil.createDefaultNodeConfigurationBuilder().build(),
				ActionLangUtil.NODE_CONFIG_MAP
		));
		JsonNode json = translator.translate(node);
		ActionNode actionNode = parseAction(json);
		if (options.isJson()) {
			System.out.println(json);
			return 0;
		}
		if (options.isCheck()) {
			return 0;
		}
		run(actionNode);
		return 0;
	}
	private static ActionNode parseAction(JsonNode json) {
		try {
			return MAPPER.treeToValue(json, ActionNode.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	private static void run(ActionNode actionNode) {
		ActionEnvironment actionEnvironment = new ActionEnvironment(
				new VariableEnvironment(),
				new InjectEnvironment.Builder()
						.add(new NanoTimeProviderEnvironment(NanoTimeProvider.SYSTEM_NANO_TIME))
						.build()
		);
		Action action = actionNode.createAction(actionEnvironment);
		while (true) {
			action.update();
			if (action.isDone()) {
				break;
			}
			try {
				Thread.sleep(3);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		action.end();
	}
}
