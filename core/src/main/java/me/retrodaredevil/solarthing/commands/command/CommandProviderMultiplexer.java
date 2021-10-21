package me.retrodaredevil.solarthing.commands.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CommandProviderMultiplexer<T extends Command> implements CommandProvider<T>{
	private final List<CommandProvider<T>> commandProviderList;

	public CommandProviderMultiplexer(Collection<? extends CommandProvider<T>> commandProviderList) {
		this.commandProviderList = Collections.unmodifiableList(new ArrayList<>(commandProviderList));
	}

	@Override
	public SourcedCommand<T> pollCommand() {
		for(CommandProvider<T> commandProvider : commandProviderList){
			SourcedCommand<T> command = commandProvider.pollCommand();
			if(command != null){
				return command;
			}
		}
		return null;
	}
}
