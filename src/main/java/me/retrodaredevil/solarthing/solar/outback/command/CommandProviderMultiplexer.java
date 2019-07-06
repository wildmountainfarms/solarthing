package me.retrodaredevil.solarthing.solar.outback.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CommandProviderMultiplexer implements CommandProvider{
	private final List<CommandProvider> commandProviderList;
	
	public CommandProviderMultiplexer(Collection<? extends CommandProvider> commandProviderList) {
		this.commandProviderList = Collections.unmodifiableList(new ArrayList<>(commandProviderList));
	}
	
	@Override
	public MateCommand pollCommand() {
		for(CommandProvider commandProvider : commandProviderList){
			MateCommand command = commandProvider.pollCommand();
			if(command != null){
				return command;
			}
		}
		return null;
	}
}
