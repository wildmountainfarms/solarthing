package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.actions.rover.RoverActionErrorState;

public class RoverErrorEnvironment {
	private final RoverActionErrorState roverActionErrorState;

	public RoverErrorEnvironment(RoverActionErrorState roverActionErrorState) {
		this.roverActionErrorState = roverActionErrorState;
	}
	public RoverErrorEnvironment() {
		this(new RoverActionErrorState());
	}

	public RoverActionErrorState getRoverActionErrorState() {
		return roverActionErrorState;
	}
}
