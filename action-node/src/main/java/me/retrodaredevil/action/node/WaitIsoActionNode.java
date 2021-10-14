package me.retrodaredevil.action.node;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.environment.ActionEnvironment;

import java.time.Duration;

/**
 * Uses the ISO-8601 standard to declare how long to wait
 */
@JsonTypeName("wait")
public class WaitIsoActionNode implements ActionNode {
	private final Duration duration;

	public WaitIsoActionNode(@JsonProperty("duration") String durationString) {
		this.duration = Duration.parse(durationString);
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return new WaitAction(duration);
	}
}
