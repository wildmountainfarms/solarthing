package me.retrodaredevil.action.lang;

import me.retrodaredevil.action.lang.translators.json.CustomNodeConfiguration;
import me.retrodaredevil.action.lang.translators.json.NodeConfiguration;
import me.retrodaredevil.action.lang.translators.json.SimpleNodeConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ActionLangUtil {
	private ActionLangUtil() { throw new UnsupportedOperationException(); }

	public static final Map<String, NodeConfiguration> NODE_CONFIG_MAP;
	static {
		Map<String, NodeConfiguration> configMap = new HashMap<>();
		configMap.put("racer", CustomNodeConfiguration.RACER);
		configMap.put("data", CustomNodeConfiguration.DATA);

		SimpleNodeConfiguration.Builder builder = createDefaultNodeConfigurationBuilder();

		// actions
		configMap.put("race", builder.copy().subNodes("racers").build());
		configMap.put("scope", builder.copy().linkedNode("action").build());
		configMap.put("act", builder.copy().args("name").linkedNode("action").build());
		configMap.put("queue", builder.copy().subNodes("actions").build());
		configMap.put("parallel", builder.copy().subNodes("actions").build());
		configMap.put("print", builder.copy().args("message").linkedNode("expression").build());
		configMap.put("call", builder.copy().args("name").build());

		configMap.put("init",     builder.copy().args("name").linkedNode("expression").build());
		configMap.put("init-exp", builder.copy().args("name").linkedNode("expression").build());
		configMap.put("set",      builder.copy().args("name").linkedNode("expression").build());
		configMap.put("set-exp",  builder.copy().args("name").linkedNode("expression").build());

		configMap.put("all",  builder.copy().linkedNode("expression").build());
		configMap.put("any",  builder.copy().linkedNode("expression").build());
		configMap.put("wait", builder.copy().args("duration").build());

		// expressions
		configMap.put("const", builder.copy().args("value").build());
		configMap.put("str", builder.copy().linkedNode("expression").build());
		configMap.put("ref", builder.copy().args("name").build());
		configMap.put("eval", builder.copy().args("name").build());
		configMap.put("join", builder.copy().linkedNode("expression").build());
		configMap.put("concat", builder.copy().subNodes("expressions").build());
		// TODO consider adding union operation and other set operations: https://www.math.net/union

		NODE_CONFIG_MAP = Collections.unmodifiableMap(configMap);
	}
	public static SimpleNodeConfiguration.Builder createDefaultNodeConfigurationBuilder() {
		// we have the ability to modify the default builder in the future if we would like to
		return SimpleNodeConfiguration.builder();
	}

}
