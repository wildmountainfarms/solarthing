package me.retrodaredevil.solarthing.commands.sequence;

import me.retrodaredevil.solarthing.commands.Command;

public interface CommandSequenceProvider<T extends Command> {
	SourcedCommandSequence<T> pollCommandSequence();
	
}
