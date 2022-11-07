package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.solarthing.actions.CommonActionUtil;
import me.retrodaredevil.solarthing.actions.chatbot.WrappedSlackChatBotActionNode;
import me.retrodaredevil.solarthing.actions.command.ExecutingCommandFeedbackActionNode;
import me.retrodaredevil.solarthing.actions.command.FlagActionNode;
import me.retrodaredevil.solarthing.actions.command.WrappedAlterManagerActionNode;
import me.retrodaredevil.solarthing.actions.homeassistant.HomeAssistantActionNode;
import me.retrodaredevil.solarthing.actions.mate.MateCommandActionNode;
import me.retrodaredevil.solarthing.actions.mate.MateCommandWaitActionNode;
import me.retrodaredevil.solarthing.actions.message.MessageSenderActionNode;
import me.retrodaredevil.solarthing.actions.message.SendMessageActionNode;
import me.retrodaredevil.solarthing.actions.rover.RoverBoostSetActionNode;
import me.retrodaredevil.solarthing.actions.rover.RoverBoostVoltageActionNode;
import me.retrodaredevil.solarthing.actions.rover.RoverLoadActionNode;
import me.retrodaredevil.solarthing.actions.rover.modbus.RoverModbusActionNode;
import me.retrodaredevil.solarthing.actions.solcast.SolcastActionNode;
import me.retrodaredevil.solarthing.actions.tracer.TracerLoadActionNode;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.config.options.ActionsOption;
import me.retrodaredevil.solarthing.config.options.CommandOption;
import me.retrodaredevil.solarthing.util.JacksonUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public final class ActionUtil {
	private ActionUtil() { throw new UnsupportedOperationException(); }

	private static final ObjectMapper CONFIG_MAPPER = ActionUtil.registerActionNodes(JacksonUtil.defaultMapper());

	public static ObjectMapper registerActionNodes(ObjectMapper objectMapper) {
		objectMapper.registerSubtypes(
				MateCommandActionNode.class,
				MateCommandWaitActionNode.class,

				RoverModbusActionNode.class,
				RoverLoadActionNode.class,
				RoverBoostSetActionNode.class,
				RoverBoostVoltageActionNode.class,

				TracerLoadActionNode.class,

				HomeAssistantActionNode.class,
				SolcastActionNode.class,

				MessageSenderActionNode.class,
				SendMessageActionNode.class,

				WrappedSlackChatBotActionNode.class,

				ExecutingCommandFeedbackActionNode.class,
				FlagActionNode.class,

				WrappedAlterManagerActionNode.class
		);
		return CommonActionUtil.registerActionNodes(objectMapper);
	}

	public static Map<String, ActionNode> getActionNodeMap(ObjectMapper objectMapper, CommandOption options) throws IOException {
		Map<String, ActionNode> actionNodeMap = new HashMap<>();
		for (Map.Entry<String, File> entry : options.getCommandFileMap().entrySet()) {
			String name = entry.getKey();
			File file = entry.getValue();
			final ActionNode actionNode = objectMapper.readValue(file, ActionNode.class);
			actionNodeMap.put(name, actionNode);
		}
		return actionNodeMap;
	}

	public static List<ActionNode> getActionNodes(ActionsOption options) throws IOException {
		List<ActionNode> actionNodes = new ArrayList<>();
		for (File file : options.getActionNodeFiles()) {
			actionNodes.add(CONFIG_MAPPER.readValue(file, ActionNode.class));
		}
		return actionNodes;
	}
}
