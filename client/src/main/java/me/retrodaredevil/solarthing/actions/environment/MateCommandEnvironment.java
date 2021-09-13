package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.OpenSource;
import me.retrodaredevil.solarthing.commands.SourcedCommand;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;

import java.util.Queue;

public class MateCommandEnvironment {
	private final OpenSource source;
	private final Queue<? super SourcedCommand<MateCommand>> queue;

	public MateCommandEnvironment(OpenSource source, Queue<? super SourcedCommand<MateCommand>> queue) {
		this.source = source;
		this.queue = queue;
	}

	@Deprecated
	public String getDeprecatedSource() {
		return source.toDataSource().toString();
	}

	/**
	 * @deprecated Use {@link SourceEnvironment#getSource()}
	 * @return The source of the person (or bot or whatever) that is requesting a command be executed. This source also contains additional information
	 */
	@Deprecated
	public OpenSource getSource() {
		return source;
	}

	public Queue<? super SourcedCommand<MateCommand>> getQueue() {
		return queue;
	}
}
