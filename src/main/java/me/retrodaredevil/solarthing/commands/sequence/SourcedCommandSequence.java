package me.retrodaredevil.solarthing.commands.sequence;

import me.retrodaredevil.solarthing.commands.Command;
import me.retrodaredevil.solarthing.commands.source.Source;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class SourcedCommandSequence<T extends Command> {
	private final Source source;
	private final CommandSequence<T> commandSequence;
	
	public SourcedCommandSequence(Source source, CommandSequence<T> commandSequence) {
		this.source = requireNonNull(source);
		this.commandSequence = requireNonNull(commandSequence);
	}
	
	public Source getSource() {
		return source;
	}
	
	public CommandSequence<T> getCommandSequence() {
		return commandSequence;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SourcedCommandSequence<?> that = (SourcedCommandSequence<?>) o;
		return Objects.equals(source, that.source) &&
			Objects.equals(commandSequence, that.commandSequence);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(source, commandSequence);
	}
}
