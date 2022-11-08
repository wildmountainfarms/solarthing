package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.expression.node.ExpressionNode;
import me.retrodaredevil.solarthing.actions.command.FlagActionNode;
import me.retrodaredevil.solarthing.actions.command.SendEncryptedActionNode;
import me.retrodaredevil.solarthing.actions.mate.ACModeActionNode;
import me.retrodaredevil.solarthing.actions.mate.AuxStateActionNode;
import me.retrodaredevil.solarthing.actions.mate.FXOperationalModeActionNode;
import me.retrodaredevil.solarthing.actions.rover.expression.RoverBoostVoltageExpressionNode;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.expression.BatteryVoltageExpressionNode;

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

				SendEncryptedActionNode.class,
				FlagActionNode.class
		);
		objectMapper.registerSubtypes(
				ExpressionNode.class,

				BatteryVoltageExpressionNode.class,
				RoverBoostVoltageExpressionNode.class
		);
		return objectMapper;
	}

}
