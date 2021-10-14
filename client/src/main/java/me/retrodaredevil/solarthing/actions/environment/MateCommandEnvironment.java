package me.retrodaredevil.action.node.environment;

import me.retrodaredevil.solarthing.commands.SourcedCommand;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;

import java.util.Queue;

public class MateCommandEnvironment {
	private final Queue<? super SourcedCommand<MateCommand>> queue;

	public MateCommandEnvironment(Queue<? super SourcedCommand<MateCommand>> queue) {
		this.queue = queue;
	}

	/**
	 * @return A mutable queue that you can add packets to. Once added to,
	 */
	public Queue<? super SourcedCommand<MateCommand>> getQueue() {
		return queue;
	}
}
