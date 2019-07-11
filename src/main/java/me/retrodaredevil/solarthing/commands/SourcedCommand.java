package me.retrodaredevil.solarthing.commands;

import me.retrodaredevil.solarthing.commands.source.Source;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class SourcedCommand<T extends Command> {
	private final Source source;
	private final T command;
	
	public SourcedCommand(Source source, T command) {
		this.source = requireNonNull(source);
		this.command = requireNonNull(command);
	}
	
	public Source getSource() {
		return source;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SourcedCommand<?> that = (SourcedCommand<?>) o;
		return Objects.equals(source, that.source) &&
			Objects.equals(command, that.command);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(source, command);
	}
	
	public T getCommand() {
		return command;
	}
}
