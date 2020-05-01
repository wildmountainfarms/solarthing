package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;

@JsonTypeName("lock")
public class LockActionNode implements ActionNode {
	private final String lockName;
	private final ActionEnvironment actionEnvironment;

	public LockActionNode(@JsonProperty("name") String lockName, @JacksonInject("environment") ActionEnvironment actionEnvironment) {
		this.lockName = lockName;
		this.actionEnvironment = actionEnvironment;
	}

	@Override
	public Action createAction() {
		return new SimpleAction(false) {
			@Override
			protected void onUpdate() {
				super.onUpdate();
				if (!actionEnvironment.isLocked(lockName)) {
					setDone(true);
					actionEnvironment.lock(lockName);
				}
			}
		};
	}
}
