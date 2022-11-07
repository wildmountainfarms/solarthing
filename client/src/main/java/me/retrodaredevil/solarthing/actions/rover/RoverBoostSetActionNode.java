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
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverWriteTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonTypeName("roverboostset")
public class RoverBoostSetActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(RoverBoostSetActionNode.class);

	private final Integer boostVoltageRaw;
	private final Integer boostTimeMinutes;
	@JsonCreator
	public RoverBoostSetActionNode(@JsonProperty("voltageraw") Integer boostVoltageRaw, @JsonProperty("minutes") Integer boostTimeMinutes) {
		this.boostVoltageRaw = boostVoltageRaw;
		this.boostTimeMinutes = boostTimeMinutes;
		if (boostVoltageRaw == null && boostTimeMinutes == null) {
			throw new IllegalArgumentException("Either voltage or minutes has to be defined!");
		}
	}
	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		RoverModbusEnvironment environment = actionEnvironment.getInjectEnvironment().get(RoverModbusEnvironment.class);
		RoverErrorEnvironment errorEnvironment = actionEnvironment.getInjectEnvironment().getOrNull(RoverErrorEnvironment.class); // This will only be null when using deprecated attach to commands

		RoverWriteTable write = environment.getWrite();
		RoverActionErrorState roverActionErrorState = errorEnvironment == null ? null : errorEnvironment.getRoverActionErrorState();

		return Actions.createRunOnce(() -> {
			try {
				// TODO have a packet for reporting this to solarthing_events
				if (boostVoltageRaw != null) {
					write.setBoostChargingVoltageRaw(boostVoltageRaw);
				}
				if (boostTimeMinutes != null) {
					write.setBoostChargingTimeMinutes(boostTimeMinutes);
				}
				if (roverActionErrorState != null) {
					roverActionErrorState.incrementSuccessCount();
				}
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Successfully executed changed boost parameters.");
			} catch (ModbusRuntimeException e) {
				LOGGER.error("Unable to perform Modbus request", e);
				if (roverActionErrorState != null) {
					roverActionErrorState.incrementErrorCount();
				}
			}
		});
	}
}
