package me.retrodaredevil.action.node.convenient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * Executes the given action as if given lockName is unlocked. That lock is locked during execution. Many times the lock name is randomly generated so that
 * the given action is only executed
 * <p>
 * Using this is easier than using a "race", a "lock", a "queue", an "unlock", and a "pass" together.
 */
@JsonTypeName("single")
public class SingleActionNode implements ActionNode {
	private final ActionNode actionNode;
	private final String lockName;

	public SingleActionNode(ActionNode actionNode, String lockName) {
		requireNonNull(this.actionNode = actionNode);
		requireNonNull(this.lockName = lockName);
	}
	@JsonCreator
	public static SingleActionNode create(
			@JsonProperty(value = "action", required = true) ActionNode actionNode,
			@JsonProperty("lock") String lockName) {
		if (lockName == null) {
			lockName = "_synchronized-" + UUID.randomUUID();
		}
		return new SingleActionNode(actionNode, lockName);
	}
	public static SingleActionNode create(ActionNode actionNode) {
		return create(actionNode, null);
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return Actions.createDynamicActionRunner(() -> {
			VariableEnvironment.LockSet globalLockSet = actionEnvironment.getVariableEnvironment().getGlobalLockSet();
			if (globalLockSet.isLocked(lockName)) {
				return Actions.createRunOnce(() -> {});
			}
			return new Actions.ActionQueueBuilder(
					Actions.createRunOnce(() -> globalLockSet.lock(lockName)),
					actionNode.createAction(actionEnvironment),
					Actions.createRunOnce(() -> globalLockSet.unlock(lockName))
			).immediatelyDoNextWhenDone(true).build();
		});
	}
}
