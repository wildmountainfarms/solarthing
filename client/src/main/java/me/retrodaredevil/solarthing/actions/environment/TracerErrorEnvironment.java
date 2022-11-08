package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.actions.error.ActionErrorState;

public class TracerErrorEnvironment {
	private final ActionErrorState actionErrorState;

	public TracerErrorEnvironment(ActionErrorState actionErrorState) {
		this.actionErrorState = actionErrorState;
	}
	public TracerErrorEnvironment() {
		this(new ActionErrorState());
	}

	public ActionErrorState getTracerActionErrorState() {
		return actionErrorState;
	}
}
