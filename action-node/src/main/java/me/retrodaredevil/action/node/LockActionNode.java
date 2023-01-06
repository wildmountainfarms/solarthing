package me.retrodaredevil.action.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;

import static java.util.Objects.requireNonNull;

@JsonTypeName("lock")
public class LockActionNode implements ActionNode {
	private final String lockName;
	private final String lockSetName;
	private final boolean unlock;

	public LockActionNode(
			@JsonProperty(value = "name", required = true) String lockName,
			@JsonProperty("set") String lockSetName,
			@JsonProperty("unlock") Boolean unlock
	) {
		this.lockName = requireNonNull(lockName);
		this.lockSetName = lockSetName;
		this.unlock = Boolean.TRUE.equals(unlock);
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return new SimpleAction(false) {
			VariableEnvironment.LockSet lockSet;
			@Override
			protected void onStart() {
				super.onStart();
				if (lockSetName == null) {
					lockSet = actionEnvironment.getVariableEnvironment().getGlobalLockSet();
				} else {
					lockSet = actionEnvironment.getVariableEnvironment().getLockSet(lockSetName);
				}
			}

			@Override
			protected void onUpdate() {
				super.onUpdate();
				if (unlock) {
					setDone(true);
					lockSet.unlock(lockName);
				} else {
					if (!lockSet.isLocked(lockName)) {
						setDone(true);
						lockSet.lock(lockName);
					}
				}
			}
		};
	}
}
