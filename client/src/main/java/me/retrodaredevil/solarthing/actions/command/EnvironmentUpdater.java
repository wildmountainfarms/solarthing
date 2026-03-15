package me.retrodaredevil.solarthing.actions.command;

import me.retrodaredevil.action.node.environment.InjectEnvironment;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface EnvironmentUpdater {
	void updateInjectEnvironment(ExecutionReason executionReason, InjectEnvironment.Builder injectEnvironmentBuilder);

	EnvironmentUpdater DO_NOTHING = (_executionReason, _injectEnvironmentBuilder) -> {};
}
