package me.retrodaredevil.action.node.convenient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;

import java.time.Duration;

@JsonTypeName("timeout")
public class TimeoutActionNode implements ActionNode {

	private final long timeoutNanos;
	private final ActionNode actionNode;

	private Long lastFireNanos = null;

	public TimeoutActionNode(
			@JsonProperty(value = "timeout", required = true) String timeoutDurationString,
			@JsonProperty(value = "action", required = true) ActionNode actionNode
	) {
		timeoutNanos = Duration.parse(timeoutDurationString).toNanos();
		this.actionNode = actionNode;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return Actions.createDynamicActionRunner(() -> {
			final Long lastFireNanos = this.lastFireNanos;
			long nowNanos = System.nanoTime();
			if (lastFireNanos == null || nowNanos - lastFireNanos > timeoutNanos) {
				this.lastFireNanos = nowNanos;
				return actionNode.createAction(actionEnvironment);
			}
			return Actions.createRunOnce(() -> {});
		});
	}
}
