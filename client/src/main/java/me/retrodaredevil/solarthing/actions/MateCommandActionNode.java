package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;

import java.util.Queue;

@JsonTypeName("matecommand")
public class MateCommandActionNode implements ActionNode {

	private final MateCommand mateCommand;
	private final Queue<? super MateCommand> queue;

	public MateCommandActionNode(@JsonProperty("command") MateCommand mateCommand, @JacksonInject("mateCommandQueue") Queue<? super MateCommand> queue) {
		this.mateCommand = mateCommand;
		this.queue = queue;
	}

	@Override
	public Action createAction() {
		return Actions.createRunOnce(() -> queue.add(mateCommand));
	}
}
