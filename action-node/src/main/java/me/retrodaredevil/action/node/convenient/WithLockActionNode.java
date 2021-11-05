package me.retrodaredevil.action.node.convenient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.LockActionNode;
import me.retrodaredevil.action.node.PassActionNode;
import me.retrodaredevil.action.node.QueueActionNode;
import me.retrodaredevil.action.node.RaceActionNode;
import me.retrodaredevil.action.node.UnlockActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;

@JsonTypeName("withlock")
public class WithLockActionNode implements ActionNode {
	private final String lockName;
	private final ActionNode action;
	private final ActionNode timeoutAction;
	private final ActionNode onTimeoutAction;
	private final ActionNode finallyAction;

	public WithLockActionNode(
			@JsonProperty("name") String lockName,
			@JsonProperty("action") ActionNode action,
			@JsonProperty("timeout") ActionNode timeoutAction,
			@JsonProperty("ontimeout") ActionNode onTimeoutAction,
			@JsonProperty("finally") ActionNode finallyAction) {
		requireNonNull(this.lockName = lockName);
		requireNonNull(this.action = action);
		this.timeoutAction = timeoutAction == null ? PassActionNode.getInstance() : timeoutAction;
		this.onTimeoutAction = onTimeoutAction == null ? PassActionNode.getInstance() : onTimeoutAction;
		this.finallyAction = finallyAction == null ? PassActionNode.getInstance() : finallyAction;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		ActionNode race = new RaceActionNode(Arrays.asList(
				new RaceActionNode.RaceNode(
						new LockActionNode(lockName),
						new QueueActionNode(Arrays.asList(action, new UnlockActionNode(lockName), finallyAction))
				),
				new RaceActionNode.RaceNode(
						timeoutAction,
						new QueueActionNode(Arrays.asList(onTimeoutAction, finallyAction))
				)
		));
		return race.createAction(actionEnvironment);
	}
}
