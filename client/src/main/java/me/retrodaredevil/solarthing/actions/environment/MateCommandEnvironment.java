package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.commands.SourcedCommand;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;

import java.util.Queue;

public class MateCommandEnvironment {
	private final String source;
	private final Queue<? super SourcedCommand<MateCommand>> queue;

	public MateCommandEnvironment(String source, Queue<? super SourcedCommand<MateCommand>> queue) {
		this.source = source;
		this.queue = queue;
	}

	public String getSource() {
		return source;
	}

	public Queue<? super SourcedCommand<MateCommand>> getQueue() {
		return queue;
	}
}
