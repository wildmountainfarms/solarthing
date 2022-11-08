package me.retrodaredevil.solarthing.actions.rover;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.io.modbus.ModbusRuntimeException;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.RoverErrorEnvironment;
import me.retrodaredevil.solarthing.actions.environment.RoverModbusEnvironment;
import me.retrodaredevil.solarthing.actions.error.ActionErrorState;
import me.retrodaredevil.solarthing.solar.renogy.rover.LoadWorkingMode;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverWriteTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.StreetLight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonTypeName("roverload")
public class RoverLoadActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(RoverLoadActionNode.class);
	private final boolean on;

	@JsonCreator
	public RoverLoadActionNode(@JsonProperty(value = "on", required = true) boolean on) {
		this.on = on;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		RoverModbusEnvironment environment = actionEnvironment.getInjectEnvironment().get(RoverModbusEnvironment.class);
		RoverErrorEnvironment errorEnvironment = actionEnvironment.getInjectEnvironment().get(RoverErrorEnvironment.class);

		RoverWriteTable write = environment.getWrite();
		ActionErrorState errorState = errorEnvironment.getRoverActionErrorState();
		return Actions.createRunOnce(() -> {
			try {
				write.setLoadWorkingMode(LoadWorkingMode.MANUAL);
				write.setStreetLightStatus(on ? StreetLight.ON : StreetLight.OFF);
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Successfully executed load command. on: " + on);
				errorState.incrementSuccessCount();
			} catch (ModbusRuntimeException e) {
				LOGGER.error("Unable to perform Modbus request", e);
				errorState.incrementErrorCount();
			}
		});
	}
}
