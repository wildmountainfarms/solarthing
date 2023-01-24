package me.retrodaredevil.action.lang;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.lang.antlr.NodeParser;
import me.retrodaredevil.action.lang.translators.json.CustomNodeConfiguration;
import me.retrodaredevil.action.lang.translators.json.JsonNodeTranslator;
import me.retrodaredevil.action.lang.translators.json.NodeConfiguration;
import me.retrodaredevil.action.lang.translators.json.SimpleConfigurationProvider;
import me.retrodaredevil.action.lang.translators.json.SimpleNodeConfiguration;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.InjectEnvironment;
import me.retrodaredevil.action.node.environment.NanoTimeProviderEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;
import org.antlr.v4.runtime.CharStreams;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;

public class ActionLangTest {

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final Map<String, NodeConfiguration> CONFIG_MAP;
	static {
		Map<String, NodeConfiguration> configMap = new HashMap<>();
		configMap.put("racer", CustomNodeConfiguration.RACER);

		// actions
		configMap.put("race", new SimpleNodeConfiguration("type", emptyList(), emptyMap(), "racers", null));
		configMap.put("scope", new SimpleNodeConfiguration("type", emptyList(), emptyMap(), null, "action"));
		configMap.put("queue", new SimpleNodeConfiguration("type", emptyList(), emptyMap(), "actions", null));
		configMap.put("print", new SimpleNodeConfiguration("type", Arrays.asList("message"), emptyMap(), null, null));
		configMap.put("log", new SimpleNodeConfiguration("type", Arrays.asList("message"), emptyMap(), null, null));
		configMap.put("call", new SimpleNodeConfiguration("type", Arrays.asList("name"), emptyMap(), null, null));
		configMap.put("init", new SimpleNodeConfiguration("type", Arrays.asList("name"), emptyMap(), null, "expression"));
		configMap.put("all", new SimpleNodeConfiguration("type", emptyList(), emptyMap(), null, "expression"));
		configMap.put("any", new SimpleNodeConfiguration("type", emptyList(), emptyMap(), null, "expression"));
		configMap.put("wait", new SimpleNodeConfiguration("type", Arrays.asList("duration"), emptyMap(), null, null));

		// expressions
		configMap.put("ref", new SimpleNodeConfiguration("type", Arrays.asList("name"), emptyMap(), null, null));
		configMap.put("eval", new SimpleNodeConfiguration("type", Arrays.asList("name"), emptyMap(), null, null));
		CONFIG_MAP = Collections.unmodifiableMap(configMap);
	}

	@Test
	void testCode() throws IOException {
		Node node = NodeParser.parseFrom(CharStreams.fromStream(
				requireNonNull(getClass().getResourceAsStream("/test_code2.ns"), "Expected file to be present!")
		));

		NodeTranslator<JsonNode> translator = new JsonNodeTranslator(new SimpleConfigurationProvider(
				new SimpleNodeConfiguration("type", emptyList(), Collections.emptyMap(), null, null),
				CONFIG_MAP
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
				new SimpleNodeConfiguration("type", emptyList(), Collections.emptyMap(), null, null),
				CONFIG_MAP
		));
		JsonNode json = translator.translate(node);
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
