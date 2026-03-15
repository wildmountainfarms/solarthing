package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.reason.ExecutionReason;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ExecutionReasonEnvironment {
	private final ExecutionReason executionReason;

	public ExecutionReasonEnvironment(ExecutionReason executionReason) {
		this.executionReason = executionReason;
	}

	public ExecutionReason getExecutionReason() {
		return executionReason;
	}
}
