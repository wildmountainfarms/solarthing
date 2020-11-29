package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;

import java.time.Duration;

import static java.util.Objects.requireNonNull;

@JsonTypeName("timeout")
public class TimeoutActionNode implements ActionNode {

	private final long timeoutMillis;
	private final ActionNode actionNode;

	private Long lastFire = null;

	public TimeoutActionNode(
			@JsonProperty("timeout") Duration timeout, @JsonProperty("millis") Long millis,
			@JsonProperty(value = "action", required = true) ActionNode actionNode
	) {
		if (timeout != null) {
			millis = timeout.toMillis();
		}
		timeoutMillis = requireNonNull(millis);
		this.actionNode = actionNode;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return Actions.createDynamicActionRunner(() -> {
			final Long lastFire = this.lastFire;
			long now = System.currentTimeMillis();
			if (lastFire == null || lastFire + timeoutMillis < now) {
				this.lastFire = now;
				return actionNode.createAction(actionEnvironment);
			}
			return Actions.createRunOnce(() -> {});
		});
	}
}
