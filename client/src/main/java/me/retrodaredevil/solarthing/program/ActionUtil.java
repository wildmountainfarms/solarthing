package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.config.options.CommandOption;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class ActionUtil {
	private ActionUtil() { throw new UnsupportedOperationException(); }
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
}
