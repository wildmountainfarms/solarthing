package me.retrodaredevil.action.lang;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.notation.Node;
import me.retrodaredevil.notation.NodeTranslator;
import me.retrodaredevil.notation.antlr.NodeParser;
import me.retrodaredevil.notation.translators.json.JsonNodeTranslator;
import me.retrodaredevil.notation.translators.json.SimpleConfigurationProvider;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.InjectEnvironment;
import me.retrodaredevil.action.node.environment.NanoTimeProviderEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;
import org.antlr.v4.runtime.CharStreams;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import static java.util.Objects.requireNonNull;

public class ActionLangTest {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Test
	void testCode() throws IOException {
		Node node = NodeParser.parseFrom(CharStreams.fromStream(
				requireNonNull(getClass().getResourceAsStream("/test_code2.ns"), "Expected file to be present!")
		));

		NodeTranslator<JsonNode> translator = new JsonNodeTranslator(new SimpleConfigurationProvider(
				ActionLangUtil.createDefaultNodeConfigurationBuilder().build(),
				ActionLangUtil.NODE_CONFIG_MAP
		));
		System.out.println(translator.translate(node));
	}
	@Test
	void runCode() throws IOException {
		runResource("/test_code3.ns");
	}

	private void runResource(String name) throws IOException {

		Node node = NodeParser.parseFrom(CharStreams.fromStream(
				requireNonNull(getClass().getResourceAsStream(name), "Expected file to be present!")
		));

		NodeTranslator<JsonNode> translator = new JsonNodeTranslator(new SimpleConfigurationProvider(
				ActionLangUtil.createDefaultNodeConfigurationBuilder().build(),
				ActionLangUtil.NODE_CONFIG_MAP
		));
		JsonNode json = translator.translate(node);
		System.out.println(json);
		ActionNode actionNode = parseAction(json);
		run(actionNode);
	}
	private ActionNode parseAction(JsonNode json) {
		try {
			return MAPPER.treeToValue(json, ActionNode.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	private void run(ActionNode actionNode) {
		Duration[] timeReference = new Duration [] { Duration.ZERO };
		ActionEnvironment actionEnvironment = new ActionEnvironment(
				new VariableEnvironment(),
				new InjectEnvironment.Builder()
						.add(new NanoTimeProviderEnvironment(() -> timeReference[0].toNanos()))
						.build()
		);
		Action action = actionNode.createAction(actionEnvironment);
		while (true) {
			action.update();
			if (action.isDone()) {
				break;
			}
			timeReference[0] = timeReference[0].plus(Duration.ofMillis(100));
		}
		System.out.println("Completed at " + timeReference[0]);
		action.end();
	}

}
