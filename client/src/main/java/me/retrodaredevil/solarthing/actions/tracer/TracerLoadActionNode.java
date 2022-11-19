package me.retrodaredevil.solarthing.actions.tracer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.io.modbus.ModbusRuntimeException;
import me.retrodaredevil.solarthing.actions.environment.TracerErrorEnvironment;
import me.retrodaredevil.solarthing.actions.environment.TracerModbusEnvironment;
import me.retrodaredevil.solarthing.actions.error.ActionErrorState;
import me.retrodaredevil.solarthing.solar.tracer.TracerWriteTable;
import me.retrodaredevil.solarthing.solar.tracer.mode.LoadControlMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonTypeName("tracerload")
public class TracerLoadActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(TracerLoadActionNode.class);

	private final boolean on;

	@JsonCreator
	public TracerLoadActionNode(@JsonProperty(value = "on", required = true) boolean on) {
		this.on = on;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		TracerModbusEnvironment tracerModbusEnvironment = actionEnvironment.getInjectEnvironment().get(TracerModbusEnvironment.class);
		TracerErrorEnvironment errorEnvironment = actionEnvironment.getInjectEnvironment().get(TracerErrorEnvironment.class);

		TracerWriteTable write = tracerModbusEnvironment.getWrite();
		ActionErrorState errorState = errorEnvironment.getTracerActionErrorState();

		return Actions.createRunOnce(() -> {
			try {
				write.setLoadControlMode(LoadControlMode.MANUAL_CONTROL);
				write.setManualLoadControlOn(on);
				LOGGER.info("Successfully executed tracer load command. on: " + on);
				errorState.incrementSuccessCount();
			} catch (ModbusRuntimeException e) {
				LOGGER.error("Unable to perform tracer write Modbus request", e);
				errorState.incrementErrorCount();
			}
		});
	}
}
