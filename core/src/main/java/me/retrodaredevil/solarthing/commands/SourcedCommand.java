package me.retrodaredevil.solarthing.commands;

import me.retrodaredevil.solarthing.type.open.OpenSource;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class SourcedCommand<T extends Command> {
	private final OpenSource source;
	private final T command;

	public SourcedCommand(OpenSource source, T command) {
		this.source = requireNonNull(source);
		this.command = requireNonNull(command);
	}

	public OpenSource getSource() {
		return source;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SourcedCommand<?> that = (SourcedCommand<?>) o;
		return source.equals(that.source) &&
				command.equals(that.command);
	}

	@Override
	public int hashCode() {
		return Objects.hash(source, command);
	}

	public T getCommand() {
		return command;
	}
}
