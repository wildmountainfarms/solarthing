package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.solarthing.actions.command.FlagActionNode;
import me.retrodaredevil.solarthing.actions.command.SendCommandActionNode;
import me.retrodaredevil.solarthing.actions.command.SendRequestHeartbeatActionNode;
import me.retrodaredevil.solarthing.actions.mate.ACModeActionNode;
import me.retrodaredevil.solarthing.actions.mate.AuxStateActionNode;
import me.retrodaredevil.solarthing.actions.mate.FXOperationalModeActionNode;
import me.retrodaredevil.solarthing.annotations.UtilityClass;

@UtilityClass
public final class CommonActionUtil {
	private CommonActionUtil() { throw new UnsupportedOperationException(); }

	public static ObjectMapper registerActionNodes(ObjectMapper objectMapper) {
		objectMapper.registerSubtypes(
				ActionNode.class,

				LogActionNode.class,

				RequiredIdentifierActionNode.class,
				RequireFullOutputActionNode.class,

				ACModeActionNode.class,
				AuxStateActionNode.class,
				FXOperationalModeActionNode.class,

				SendCommandActionNode.class,
				SendRequestHeartbeatActionNode.class,
				FlagActionNode.class
		);
		return objectMapper;
	}

}
