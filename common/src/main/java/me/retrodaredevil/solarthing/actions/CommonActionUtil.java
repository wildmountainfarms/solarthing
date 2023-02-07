package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.lang.ActionLangUtil;
import me.retrodaredevil.action.lang.Node;
import me.retrodaredevil.action.lang.NodeTranslator;
import me.retrodaredevil.action.lang.antlr.NodeParser;
import me.retrodaredevil.action.lang.translators.json.JsonNodeTranslator;
import me.retrodaredevil.action.lang.translators.json.NodeConfiguration;
import me.retrodaredevil.action.lang.translators.json.SimpleConfigurationProvider;
import me.retrodaredevil.action.lang.translators.json.SimpleNodeConfiguration;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.expression.node.ExpressionNode;
import me.retrodaredevil.solarthing.actions.command.FlagActionNode;
import me.retrodaredevil.solarthing.actions.command.SendEncryptedActionNode;
import me.retrodaredevil.solarthing.actions.config.ActionFormat;
import me.retrodaredevil.solarthing.actions.config.ActionReference;
import me.retrodaredevil.solarthing.actions.mate.ACModeActionNode;
import me.retrodaredevil.solarthing.actions.mate.AuxStateActionNode;
import me.retrodaredevil.solarthing.actions.mate.FXOperationalModeActionNode;
import me.retrodaredevil.solarthing.actions.rover.expression.RoverBoostVoltageExpressionNode;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.expression.BatteryVoltageExpressionNode;
import me.retrodaredevil.solarthing.expression.NetChargeExpressionNode;
import org.antlr.v4.runtime.CharStreams;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public final class CommonActionUtil {
	private CommonActionUtil() { throw new UnsupportedOperationException(); }

	private static final Logger LOGGER = LoggerFactory.getLogger(CommonActionUtil.class);
	private static final NodeTranslator<JsonNode> TRANSLATOR;
	static {
		Map<String, NodeConfiguration> configMap = new HashMap<>(ActionLangUtil.NODE_CONFIG_MAP);
		SimpleNodeConfiguration.Builder builder = SimpleNodeConfiguration.builder();

		configMap.put("log", builder.copy().args("message").build());
		configMap.put("with-lock", builder.copy().rename("withlock").args("name").linkedNode("action").build());
		configMap.put("aux-state", builder.copy().rename("auxstate").build());
		configMap.put("ac-mode", builder.copy().rename("acmode").build());
		configMap.put("fx-operational", builder.copy().rename("fxoperational").build());
		configMap.put("chatbot-slack", builder.copy().rename("chatbot_slack").build());

		TRANSLATOR = new JsonNodeTranslator(new SimpleConfigurationProvider(
				builder.build(),
				configMap
		));
	}


	@Contract("null -> fail; _ -> param1")
	public static ObjectMapper registerActionNodes(ObjectMapper objectMapper) {
		objectMapper.registerSubtypes(
				ActionNode.class,

				LogActionNode.class,

				RequiredIdentifierActionNode.class,
				RequireFullOutputActionNode.class,

				ACModeActionNode.class,
				AuxStateActionNode.class,
				FXOperationalModeActionNode.class,

				SendEncryptedActionNode.class,
				FlagActionNode.class
		);
		objectMapper.registerSubtypes(
				ExpressionNode.class,

				BatteryVoltageExpressionNode.class,
				NetChargeExpressionNode.class,
				RoverBoostVoltageExpressionNode.class
		);
		return objectMapper;
	}
	public static ActionNode readActionReference(ObjectMapper objectMapper, ActionReference actionReference) throws IOException {
		ActionFormat actionFormat = actionReference.getFormat();
		if (actionFormat == ActionFormat.RAW_JSON) {
//			return objectMapper.readValue(actionReference.getFile(), ActionNode.class);
			return objectMapper.readValue(Files.newInputStream(actionReference.getPath()), ActionNode.class);
		} else if (actionFormat == ActionFormat.NOTATION_SCRIPT) {
			Node node = NodeParser.parseFrom(CharStreams.fromPath(actionReference.getPath(), StandardCharsets.UTF_8));
			JsonNode jsonNode = TRANSLATOR.translate(node);
			LOGGER.debug("Compiled notation script file to JSON (below). path: " + actionReference.getPath() + "\n" + jsonNode);
			return objectMapper.treeToValue(jsonNode, ActionNode.class);
		} else throw new AssertionError("Unknown ActionFormat: " + actionFormat);
	}

}
