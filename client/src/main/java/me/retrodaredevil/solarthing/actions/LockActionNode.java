package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;

@JsonTypeName("lock")
public class LockActionNode implements ActionNode {
	private final String lockName;

	public LockActionNode(@JsonProperty("name") String lockName) {
		this.lockName = lockName;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return new SimpleAction(false) {
			@Override
			protected void onUpdate() {
				super.onUpdate();
				if (!actionEnvironment.getGlobalEnvironment().isLocked(lockName)) {
					setDone(true);
					actionEnvironment.getGlobalEnvironment().lock(lockName);
				}
			}
		};
	}
}
