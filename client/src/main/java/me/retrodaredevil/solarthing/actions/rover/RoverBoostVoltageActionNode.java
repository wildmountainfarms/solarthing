package me.retrodaredevil.solarthing.actions.rover;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated(forRemoval = true)
@JsonTypeName("roverboostvoltage")
public class RoverBoostVoltageActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(RoverBoostVoltageActionNode.class);

	private final int boostVoltageRaw;
	private final boolean not;
	private final RoverMatcher roverMatcher;

	@JsonCreator
	public RoverBoostVoltageActionNode(
			@JsonProperty(value = "voltageraw", required = true) int boostVoltageRaw,
			@JsonProperty("not") Boolean not,
			@JsonProperty("fragment") Integer fragmentId,
			@JsonProperty("number") Integer number
	) {
		this.boostVoltageRaw = boostVoltageRaw;
		this.not = Boolean.TRUE.equals(not);
		roverMatcher = RoverMatcher.createFromRaw(fragmentId, number);
	}
	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		RoverMatcher.Provider provider = roverMatcher.createProvider(actionEnvironment.getInjectEnvironment());
		return new SimpleAction(false) {
			@Override
			protected void onUpdate() {
				super.onUpdate();
				RoverStatusPacket roverStatusPacket = provider.get();
				if (roverStatusPacket == null) {
					LOGGER.warn("Couldn't find rover status packet!");
					return;
				}
				setDone((roverStatusPacket.getBoostChargingVoltageRaw() == boostVoltageRaw) != not);
			}
		};
	}
}
