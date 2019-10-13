package me.retrodaredevil.solarthing.commands;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class SourcedCommand<T extends Command> {
	private final String source;
	private final T command;
	
	public SourcedCommand(String source, T command) {
		this.source = requireNonNull(source);
		this.command = requireNonNull(command);
	}
	
	public String getSource() {
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
