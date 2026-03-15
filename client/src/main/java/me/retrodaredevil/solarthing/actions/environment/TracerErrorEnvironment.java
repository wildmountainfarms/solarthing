package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.actions.error.ActionErrorState;
import org.jspecify.annotations.NullMarked;

@NullMarked
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
