package me.retrodaredevil.action.lang;

import com.fasterxml.jackson.databind.JsonNode;
import me.retrodaredevil.action.lang.antlr.NodeParser;
import me.retrodaredevil.action.lang.translators.json.CustomNodeConfiguration;
import me.retrodaredevil.action.lang.translators.json.JsonNodeTranslator;
import me.retrodaredevil.action.lang.translators.json.NodeConfiguration;
import me.retrodaredevil.action.lang.translators.json.SimpleConfigurationProvider;
import me.retrodaredevil.action.lang.translators.json.SimpleNodeConfiguration;
import org.antlr.v4.runtime.CharStreams;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;

public class ActionLangTest {
	@Test
	void testCode() throws IOException {
		Node node = NodeParser.parseFrom(CharStreams.fromStream(
				requireNonNull(getClass().getResourceAsStream("/test_code2.txt"), "Expected file to be present!")
		));
		Map<String, NodeConfiguration> configMap = new HashMap<>();
		configMap.put("racer", CustomNodeConfiguration.RACER);
		configMap.put("scope", new SimpleNodeConfiguration("type", emptyList(), emptyMap(), null, "action"));
		configMap.put("queue", new SimpleNodeConfiguration("type", emptyList(), emptyMap(), "actions", null));
		configMap.put("print", new SimpleNodeConfiguration("type", Arrays.asList("message"), emptyMap(), null, null));
		configMap.put("log", new SimpleNodeConfiguration("type", Arrays.asList("message"), emptyMap(), null, null));
		configMap.put("call", new SimpleNodeConfiguration("type", Arrays.asList("name"), emptyMap(), null, null));
		NodeTranslator<JsonNode> translator = new JsonNodeTranslator(new SimpleConfigurationProvider(
				new SimpleNodeConfiguration("type", emptyList(), Collections.emptyMap(), null, null),
				configMap
		));
		System.out.println(translator.translate(node));
	}

}
