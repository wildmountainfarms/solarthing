package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.solarthing.actions.CommonActionUtil;
import me.retrodaredevil.solarthing.actions.chatbot.WrappedSlackChatBotActionNode;
import me.retrodaredevil.solarthing.actions.command.ExecutingCommandFeedbackActionNode;
import me.retrodaredevil.solarthing.actions.command.FlagActionNode;
import me.retrodaredevil.solarthing.actions.command.WrappedAlterManagerActionNode;
import me.retrodaredevil.solarthing.actions.config.ActionReference;
import me.retrodaredevil.solarthing.actions.file.WriteTextActionNode;
import me.retrodaredevil.solarthing.actions.homeassistant.HomeAssistantActionNode;
import me.retrodaredevil.solarthing.actions.mate.MateCommandActionNode;
import me.retrodaredevil.solarthing.actions.mate.MateCommandWaitActionNode;
import me.retrodaredevil.solarthing.actions.message.MessageSenderActionNode;
import me.retrodaredevil.solarthing.actions.message.SendMessageActionNode;
import me.retrodaredevil.solarthing.actions.rover.RoverBoostSetActionNode;
import me.retrodaredevil.solarthing.actions.rover.RoverLoadActionNode;
import me.retrodaredevil.solarthing.actions.rover.modbus.RoverModbusActionNode;
import me.retrodaredevil.solarthing.actions.solcast.SolcastActionNode;
import me.retrodaredevil.solarthing.actions.tracer.TracerLoadActionNode;
import me.retrodaredevil.solarthing.actions.tracer.modbus.TracerModbusActionNode;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.config.options.ActionConfig;
import me.retrodaredevil.solarthing.config.options.ActionsOption;
import me.retrodaredevil.solarthing.config.options.CommandOption;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public final class ActionUtil {
	private ActionUtil() { throw new UnsupportedOperationException(); }
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUtil.class);

	private static final ObjectMapper CONFIG_MAPPER = ActionUtil.registerActionNodes(JacksonUtil.defaultMapper());

	@Contract("null -> fail; _ -> param1")
	public static ObjectMapper registerActionNodes(ObjectMapper objectMapper) {
		objectMapper.registerSubtypes(
				MateCommandActionNode.class,
				MateCommandWaitActionNode.class,

				RoverModbusActionNode.class,
				RoverLoadActionNode.class,
				RoverBoostSetActionNode.class,

				TracerModbusActionNode.class,
				TracerLoadActionNode.class,

				HomeAssistantActionNode.class,
				SolcastActionNode.class,

				WriteTextActionNode.class,

				MessageSenderActionNode.class,
				SendMessageActionNode.class,

				WrappedSlackChatBotActionNode.class,

				ExecutingCommandFeedbackActionNode.class,
				FlagActionNode.class,

				WrappedAlterManagerActionNode.class
		);
		return CommonActionUtil.registerActionNodes(objectMapper);
	}

	public static Map<String, ActionNode> createCommandNameToActionNodeMap(CommandOption options) throws IOException {
		Map<String, ActionNode> actionNodeMap = new HashMap<>();
		for (Map.Entry<String, ActionReference> entry : options.getCommandNameToActionReferenceMap().entrySet()) {
			String name = entry.getKey();
			ActionReference actionReference = entry.getValue();
			actionNodeMap.put(name, CommonActionUtil.readActionReference(CONFIG_MAPPER, actionReference));
		}
		return actionNodeMap;
	}

	public static List<ActionNodeEntry> createActionNodeEntries(ActionsOption options) throws IOException {
		List<ActionNodeEntry> actionNodeEntries = new ArrayList<>();
		for (ActionConfig.Entry entry : options.getActionConfig().getEntries()) {
			ActionNode actionNode = CommonActionUtil.readActionReference(CONFIG_MAPPER, entry.getActionReference());
			boolean runOnce = entry.isRunOnce();
			actionNodeEntries.add(new ActionNodeEntry(actionNode, runOnce));
		}
		return actionNodeEntries;
	}
}
