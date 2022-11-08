package me.retrodaredevil.solarthing.commands.command;

import me.retrodaredevil.solarthing.reason.ExecutionReason;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class SourcedCommand<T extends Command> {
	private final ExecutionReason executionReason;
	private final T command;

	public SourcedCommand(ExecutionReason executionReason, T command) {
		this.executionReason = requireNonNull(executionReason);
		this.command = requireNonNull(command);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SourcedCommand<?> that = (SourcedCommand<?>) o;
		return executionReason.equals(that.executionReason) &&
				command.equals(that.command);
	}

	@Override
	public int hashCode() {
		return Objects.hash(executionReason, command);
	}

	public T getCommand() {
		return command;
	}

	public ExecutionReason getExecutionReason() {
		return executionReason;
	}
}
