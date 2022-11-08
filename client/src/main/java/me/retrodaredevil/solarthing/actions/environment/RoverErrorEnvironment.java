package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.actions.error.ActionErrorState;

public class RoverErrorEnvironment {
	private final ActionErrorState actionErrorState;

	public RoverErrorEnvironment(ActionErrorState actionErrorState) {
		this.actionErrorState = actionErrorState;
	}
	public RoverErrorEnvironment() {
		this(new ActionErrorState());
	}

	public ActionErrorState getRoverActionErrorState() {
		return actionErrorState;
	}
}
