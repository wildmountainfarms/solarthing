package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.reason.ExecutionReason;

public final class ExecutionReasonEnvironment {
	private final ExecutionReason executionReason;

	public ExecutionReasonEnvironment(ExecutionReason executionReason) {
		this.executionReason = executionReason;
	}

	public ExecutionReason getExecutionReason() {
		return executionReason;
	}
}
